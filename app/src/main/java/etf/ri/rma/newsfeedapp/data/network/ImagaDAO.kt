package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import android.util.Base64
import android.util.Patterns
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.tagoviSlikaKes
import etf.ri.rma.newsfeedapp.network.RetrofitClient
import etf.ri.rma.newsfeedapp.extrastuff.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagaDAO {
    private var api = RetrofitClient.imagaApi

    private val API_KEY = "acc_d21d78c3bb6540b"
    private val API_SECRET = "b994703c3410c3ddd019570c3d43eda0"

    companion object {
        @Volatile
        private var INSTANCE: ImagaDAO? = null

        @Volatile
        private var testApiService: ImagaApiService? = null

        fun getInstance(): ImagaDAO {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ImagaDAO().also { INSTANCE = it }
            }
        }

        fun setTestApiService(api: ImagaApiService?) {
            testApiService = api
            getInstance().setApiService(api ?: RetrofitClient.imagaApi)
        }

        suspend fun getTags(context: Context, imageURL: String): List<String> =
            getInstance().getTags(context, imageURL)

        suspend fun getTagsForNewsItem(context: Context, uuid: String, imageURL: String): List<String> =
            getInstance().getTagsForNewsItem(context, uuid, imageURL)
    }

    fun setApiService(imagaApiService: ImagaApiService) {
        this.api = imagaApiService
    }

    init {
        this.api = testApiService ?: RetrofitClient.imagaApi
    }

    private fun getAuthHeader(): String {
        val credentials = "$API_KEY:$API_SECRET"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }

    suspend fun getTags(context: Context, imageURL: String): List<String> = withContext(Dispatchers.IO) {
        if (!Patterns.WEB_URL.matcher(imageURL).matches()) {
            throw InvalidImageURLException("Invalid image URL: $imageURL")
        }

        val hasInternet = NetworkUtils.hasInternetConnection(context)

        // Provjeri keš
        tagoviSlikaKes[imageURL]?.let {
            return@withContext it
        }

        // Ako nema interneta, vrati prazan niz
        if (!hasInternet) {
            return@withContext emptyList()
        }

        try {
            val response = api.getTagsForImage(getAuthHeader(), imageURL)

            val tags = response.result.tags.mapNotNull {
                it.tag["en"]
            }

            if (tags.isNotEmpty()) {
                tagoviSlikaKes[imageURL] = tags
            }

            return@withContext tags
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    // Nova metoda koja automatski sinkronizira s bazom
    suspend fun getTagsForNewsItem(context: Context, uuid: String, imageURL: String): List<String> = withContext(Dispatchers.IO) {
        val hasInternet = NetworkUtils.hasInternetConnection(context)

        // Ako nema interneta, pokušaj dobiti tagove iz baze
        if (!hasInternet) {
            val tagsFromDb = NavigationState.getTagsHybrid(uuid, false)
            if (tagsFromDb.isNotEmpty()) {
                return@withContext tagsFromDb
            }
        }

        // Provjeri keš ili dohvati s web servisa
        val tags = getTags(context, imageURL)

        // Ako su tagovi uspješno dohvaćeni, sinkroniziraj s bazom
        if (tags.isNotEmpty()) {
            NavigationState.syncTagsWithDatabase(uuid, tags)
        }

        return@withContext tags
    }
}