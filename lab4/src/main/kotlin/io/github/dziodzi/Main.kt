package io.github.dziodzi

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDate

private val log = LoggerFactory.getLogger("MainLogger")

fun main() {
    runBlocking {
        log.debug("Running application")
        val count = 100;
        val period = LocalDate.now().minusDays(90)..LocalDate.now()

        val newsList = getNews(count = count)
        log.info("Successfully received news")
        val mostRatedNews = newsList.getMostRatedNews(count = count, period = period)
        log.info("Successfully rated news")

        saveNewsToCsv("news.csv", mostRatedNews)
        saveNewsToMarkdown("news.md", mostRatedNews)

        log.debug("Shutting down the application")
    }
}
