package etf.ri.rma.newsfeedapp.network

import com.google.gson.annotations.SerializedName
import etf.ri.rma.newsfeedapp.model.NewsItem

data class NewsResponse(
    @SerializedName("items") val items: List<NewsItemDto>
)

data class NewsItemDto(
    @SerializedName("id") val uuid: String,
    @SerializedName("newsInfo") val newsInfo: NewsInfoDto
)

data class NewsInfoDto(
    @SerializedName("title") val title: String?,
    @SerializedName("snippet") val snippet: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("isFeatured") val isFeatured: Boolean?,
    @SerializedName("source") val source: String?,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("imageTags") val imageTags: List<String>?
)

fun NewsItemDto.toNewsItem(): NewsItem {
    return NewsItem(
        uuid = uuid,
        title = newsInfo.title ?: "Untitled",
        snippet = newsInfo.snippet ?: "No snippet available",
        imageUrl = newsInfo.imageUrl,
        category = newsInfo.category ?: "General",
        isFeatured = newsInfo.isFeatured ?: false,
        source = newsInfo.source ?: "Unknown",
        publishedDate = newsInfo.publishedDate ?: "Unknown date",
        imageTags = ArrayList(newsInfo.imageTags ?: emptyList())
    )
}