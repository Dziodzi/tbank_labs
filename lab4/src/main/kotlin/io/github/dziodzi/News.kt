package io.github.dziodzi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
data class News(
    val id: Int,
    @SerialName("title") val title: String,
    @SerialName("publication_date") val publicationDate: Long,
    val place: String? = null,
    @SerialName("description") val description: String,
    @SerialName("site_url") val siteUrl: String? = null,
    @SerialName("favorites_count") val favoritesCount: Int = 0,
    @SerialName("comments_count") val commentsCount: Int = 0
) {
    @SerialName("rating") val rating: Double
        get() = 1 / (1 + exp(-(favoritesCount / (commentsCount + 1).toDouble())))
}

@Serializable
data class NewsResponse(val results: List<News>)
