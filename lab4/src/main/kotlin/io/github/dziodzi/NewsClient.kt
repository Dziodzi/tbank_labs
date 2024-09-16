package io.github.dziodzi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ServerRequestException(message: String) : Exception(message)

private val client = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

suspend fun getNews(count: Int = 100): List<News> {
    return try {
        val response: HttpResponse = client.get("https://kudago.com/public-api/v1.4/news/") {
            parameter("page", 1)
            parameter("page_size", count)
            parameter("order_by", "-publication_date")
            parameter("actual_only", true)
            parameter("location", "spb")
            parameter("fields", "id,publication_date,title,description,site_url,favorites_count,comments_count")
        }

        if (response.status == HttpStatusCode.OK) {
            val responseBody: String = response.body()
            Json { ignoreUnknownKeys = true }.decodeFromString<NewsResponse>(responseBody).results
        } else {
            throw ServerRequestException("Unexpected response status: ${response.status}")
        }
    } catch (e: ServerRequestException) {
        throw e
    } catch (e: Exception) {
        throw ServerRequestException("Failed to get news: ${e.message}")
    }
}
