package io.github.dziodzi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class ServerRequestException(message: String) : Exception(message)

private val log = LoggerFactory.getLogger("MainLogger")

val client = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

private val json = Json { ignoreUnknownKeys = true }

private suspend fun fetchNews(pageNumber: Int): List<News> {
    return try {
        val response: HttpResponse = client.get(Config.BASE_URL) {
            parameter("page", pageNumber)
            parameter("page_size", Config.PAGE_SIZE)
            Config.requestParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }

        if (response.status == HttpStatusCode.OK) {
            val responseBody: String = response.body()
            Json { ignoreUnknownKeys = true }.decodeFromString<NewsResponse>(responseBody).results
        } else {
            log.error("Failed to fetch news from page $pageNumber: ${response.status}")
            emptyList()
        }
    } catch (e: Exception) {
        log.error("Error fetching news from page $pageNumber: ${e.message}")
        emptyList()
    }
}

suspend fun getNews(count: Int = Config.NEWS_COUNT): List<News> {
    val newsList = mutableListOf<News>()
    var currentPage = 1

    try {
        while (newsList.size < count) {
            val pageNews = fetchNews(currentPage)

            if (pageNews.isEmpty()) {
                break
            }

            newsList.addAll(pageNews)
            log.info("Fetched ${pageNews.size} news from page $currentPage. Total news: ${newsList.size}")
            currentPage++
        }

        return newsList.take(count)
    } catch (e: ServerRequestException) {
        throw e
    } catch (e: Exception) {
        throw ServerRequestException("Failed to get news: ${e.message}")
    }
}

sealed class NewsMessage
data class AddNews(val news: List<News>) : NewsMessage()
class GetNews(val response: CompletableDeferred<List<News>>) : NewsMessage()

suspend fun getNewsParallel(targetCount: Int, dispatcher: CoroutineContext): List<News> = coroutineScope {
    val semaphore = Semaphore(Config.MAX_PARALLEL_REQUESTS)

    val newsActor = actor<NewsMessage>(dispatcher) {
        val newsList = mutableListOf<News>()

        for (msg in channel) {
            when (msg) {
                is AddNews -> newsList.addAll(msg.news)
                is GetNews -> msg.response.complete(newsList.take(targetCount))
            }
        }
    }

    val jobs = (1..Int.MAX_VALUE).map { pageNumber ->
        async(dispatcher) {
            semaphore.withPermit {
                val pageNews = fetchNews(pageNumber)
                if (pageNews.isNotEmpty()) {
                    log.info("Fetched ${pageNews.size} news from page $pageNumber")
                    newsActor.send(AddNews(pageNews))
                }
            }
        }
    }

    jobs.take(targetCount / Config.PAGE_SIZE + 1).forEach { it.await() }

    val response = CompletableDeferred<List<News>>()
    newsActor.send(GetNews(response))
    newsActor.close()

    return@coroutineScope response.await()
}
