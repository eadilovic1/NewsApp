package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class ImagaResult(
    val tags: List<ImagaTag>
)

data class ImagaTagResult(
    val result: ImagaResult
)

data class ImagaTag(
    val tag: Map<String, String>,
    val confidence: Double
)

interface ImagaApiService {
    @GET("v2/tags")
    suspend fun getTagsForImage(
        @Header("Authorization") authHeader: String,
        @Query("image_url") imageUrl: String
    ): ImagaTagResult
}