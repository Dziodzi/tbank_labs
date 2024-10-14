package io.github.dziodzi

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("ParallelFetcherLogger")

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()

    val newsList = getNewsParallel(Config.NEWS_COUNT)
    log.info("Successfully received news")

    val mostRatedNews = newsList.getMostRatedNews(count = Config.NEWS_COUNT, period = Config.PERIOD)
    log.info("Successfully rated news")

    saveNewsToCsv("news_parallel.csv", mostRatedNews)
    saveNewsToMarkdown("news_parallel.md", mostRatedNews)

    val endTime = System.currentTimeMillis()
    log.info("Total execution time: ${endTime - startTime} ms")
}