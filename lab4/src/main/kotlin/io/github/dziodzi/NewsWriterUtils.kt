package io.github.dziodzi

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("WriterUtilsLogger")

fun canWriteToFile(file: File): Boolean {
    return try {
        file.parentFile?.mkdirs()
        file.createNewFile() || file.canWrite()
    } catch (e: IOException) {
        log.error("Cannot write to file: ${file.absolutePath}", e)
        false
    }
}

fun formatPublicationDate(publicationDate: Long): String {
    val instant = Instant.ofEpochSecond(publicationDate)
    return instant.atZone(ZoneId.systemDefault()).toLocalDate().toString()
}

fun saveNewsToCsv(filename: String, news: Collection<News>) {
    val path = Paths.get("lab4", "src", "main", "kotlin", "io", "github", "dziodzi", "output", filename).toString()
    val file = File(path)

    if (file.exists()) {
        log.warn("File $path already exists")
        return
    }

    if (!canWriteToFile(file)) {
        log.error("Cannot write to file $path")
        return
    }

    try {
        csvWriter().open(file) {
            writeRow(
                "id",
                "rating",
                "publication_date",
                "title",
                "description",
                "site_url",
                "favorites_count",
                "comments_count"
            )
            news.forEach {
                writeRow(
                    it.id,
                    it.rating,
                    formatPublicationDate(it.publicationDate),
                    it.title,
                    it.description ?: "",
                    it.siteUrl,
                    it.favoritesCount,
                    it.commentsCount
                )
            }
            log.info("News data saved successfully to CSV file at the path: $path")
        }
    } catch (e: Exception) {
        log.error("Failed to save news to CSV file: $path", e)
    }
}

fun saveNewsToMarkdown(filename: String, news: Collection<News>) {
    val path = Paths.get("lab4", "src", "main", "kotlin", "io", "github", "dziodzi", "output", filename).toString()
    val file = File(path)

    if (file.exists()) {
        log.warn("File $path already exists")
        return
    }

    if (!canWriteToFile(file)) {
        log.error("Cannot write to file $path")
        return
    }

    try {
        val newsString = readme {
            header(level = 1) { +"News" }
            news.forEach {
                val headerText = "Publication date: ${formatPublicationDate(it.publicationDate)} (id: ${it.id})"
                header(level = 2) { +it.title }
                header(level = 3) { +headerText }
                text { +it.description }
                text { +"\n > Rating: ${it.rating}\n" }
                list {
                    item("Favorites Count: ${it.favoritesCount}\n")
                    item("Comments Count: ${it.commentsCount}\n")
                }
                link(it.siteUrl.toString(), "Open in web")
                divider()
            }
        }

        file.writeText(newsString)
        log.info("News data saved successfully to Markdown file at the path: $path")
    } catch (e: Exception) {
        log.error("Failed to save news to Markdown file: $path", e)
    }
}
