package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun <T> createService(
        baseUrl: String,
        serviceClass: Class<T>
    ): T {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    val newsApi: NewsApiService by lazy {
        createService("https://api.thenewsapi.com/v1/", NewsApiService::class.java)
    }

    val imagaApi: ImagaApiService by lazy {
        createService("https://api.imagga.com/", ImagaApiService::class.java)
    }
}