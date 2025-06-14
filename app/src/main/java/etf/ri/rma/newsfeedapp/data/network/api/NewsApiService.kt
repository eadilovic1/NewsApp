package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class NewsApiResponse(
    val data: List<NewsApiItem>
)

data class NewsApiItem(
    val uuid: String,
    val title: String,
    val description: String,
    val snippet: String,
    val url: String,
    val image_url: String?,
    val language: String,
    val published_at: String,
    val source: String,
    val categories: List<String>
)

interface NewsApiService {
    @GET("news/top")
    suspend fun getTopStoriesByCategory(
        @Query("api_token") apiToken: String,
        @Query("categories") categories: String,
        @Query("limit") limit: Int = 3,
        @Query("language") language: String = "en"
    ): Response<NewsApiResponse>

    @GET("news/similar/{uuid}")
    suspend fun getSimilarStories(
        @Path("uuid") uuid: String,
        @Query("api_token") apiToken: String,
        @Query("limit") limit: Int = 2
    ): Response<NewsApiResponse>

    @GET("news/all")
    suspend fun getHeadlinesBySource(
        @Query("api_token") apiToken: String,
        @Query("domains") sourceIds: String
    ): Response<NewsApiResponse>

}