package io.github.dziodzi

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun List<News>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<News> {
    return asSequence()
        .filter { news ->
            val instant = Instant.ofEpochSecond(news.publicationDate)
            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            localDate in period
        }
        .sortedByDescending { it.rating }
        .take(count)
        .toList()
}
