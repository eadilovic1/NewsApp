package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiItem
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.network.RetrofitClient
import etf.ri.rma.newsfeedapp.extrastuff.NetworkUtils
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.nasloviSaIstogIzvora
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.slicneVijestiKes
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.trenutneSveVijesti
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.updateVrijemePoziva
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.vrijemePozivaPoKategoriji
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class NewsDAO {
    private var api = RetrofitClient.newsApi
    private val API_KEY = "g2Fs9Biwwrr73mN0et07BeTr0ln3oHs9jMmww7nG"

    init {
        this.api = testApiService ?: RetrofitClient.newsApi
    }

    companion object {
        @Volatile
        private var INSTANCE: NewsDAO? = null

        @Volatile
        private var testApiService: NewsApiService? = null

        fun getInstance(): NewsDAO {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NewsDAO().also { INSTANCE = it }
            }
        }

        fun setTestApiService(api: NewsApiService?) {
            testApiService = api
            getInstance().setApiService(api ?: RetrofitClient.newsApi)
        }

        fun getAllNews(): List<NewsItem> = getInstance().getAllNews()

        suspend fun getTopStoriesByCategory(context: Context, category: String): List<NewsItem> =
            getInstance().getTopStoriesByCategory(context, category)

        suspend fun getSimilarStories(context: Context, uuid: String): List<NewsItem> =
            getInstance().getSimilarStories(context, uuid)

        suspend fun getHeadlinesBySource(context: Context, sourceid: String): List<String> =
            getInstance().getHeadlinesBySource(context, sourceid)

        fun addNewsItem(newsItem: NewsItem, kategorija: String) =
            getInstance().addNewsItem(newsItem, kategorija)
    }

    fun setApiService(newsApiService: NewsApiService) {
        this.api = newsApiService
    }

    fun getAllNews(): List<NewsItem> {
        return trenutneSveVijesti.toList()
    }

    fun addNewsItem(newsItem: NewsItem, kategorija: String) {
        if (trenutneSveVijesti.none { it.uuid == newsItem.uuid }) {
            trenutneSveVijesti.add(newsItem.copyByCategory(kategorija))
        }
    }

    private fun mapApiItemToNewsItem(item: NewsApiItem, kategorija: String? = null): NewsItem {
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDate = try {
            ZonedDateTime.parse(item.published_at).format(outputFormatter)
        } catch (e: Exception) {
            item.published_at
        }

        return NewsItem(
            uuid = item.uuid,
            title = item.title,
            snippet = item.snippet,
            imageUrl = item.image_url,
            category = kategorija ?: item.categories.firstOrNull()?.lowercase() ?: "",
            isFeatured = false,
            source = item.source,
            publishedDate = formattedDate,
            imageTags = arrayListOf()
        )
    }

    suspend fun getTopStoriesByCategory(context: Context, category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        val kategorija = category.lowercase(Locale.ROOT)
        val trenutno = System.currentTimeMillis()
        val proslo = vrijemePozivaPoKategoriji[kategorija] ?: 0L

        val hasInternet = NetworkUtils.hasInternetConnection(context)

        if (kategorija == "All") {
            return@withContext NavigationState.getNewsHybrid("All", hasInternet)
        }

        if (!hasInternet) {
            return@withContext NavigationState.getNewsHybrid(kategorija, false)
        }

        val lokalne = trenutneSveVijesti.filter { it.category == kategorija }

        if (trenutno - proslo < TimeUnit.SECONDS.toMillis(30)) {
            updateVrijemePoziva(kategorija)
            return@withContext lokalne
        }

        try {
            val odgovor = api.getTopStoriesByCategory(apiToken = API_KEY, categories = kategorija)

            val noveVijesti = if (odgovor.isSuccessful) {
                odgovor.body()?.data
                    ?.map { mapApiItemToNewsItem(it, kategorija) }
                    ?.filter { it.category == kategorija }
                    ?.take(3)
            } else null

            if (noveVijesti != null) {
                val nepostojeci = noveVijesti.filter { nova -> trenutneSveVijesti.none { it.uuid == nova.uuid } }

                trenutneSveVijesti.replaceAll { vijest ->
                    if (vijest.category == kategorija && nepostojeci.none { it.uuid == vijest.uuid }) {
                        vijest.copyByType(false)
                    } else vijest
                }
                trenutneSveVijesti.addAll(0, nepostojeci.map { it.copyByType(true) })

                nepostojeci.forEach { newsItem ->
                    NavigationState.syncNewsWithDatabase(newsItem)
                }
            }

            updateVrijemePoziva(kategorija)
            return@withContext trenutneSveVijesti.filter { it.category == kategorija }

        } catch (e: Exception) {
            return@withContext NavigationState.getNewsHybrid(kategorija, false)
        }
    }

    suspend fun getSimilarStories(context: Context, uuid: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            UUID.fromString(uuid)
        } catch (e: Exception) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        val hasInternet = NetworkUtils.hasInternetConnection(context)

        if (!hasInternet) {
            return@withContext NavigationState.getSimilarNewsHybrid(uuid, false)
        }

        if (slicneVijestiKes.containsKey(uuid)) {
            return@withContext slicneVijestiKes[uuid]!!
        }

        try {
            val response = api.getSimilarStories(uuid = uuid, apiToken = API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val slicne = response.body()!!.data.map { mapApiItemToNewsItem(it) }.take(2)
                slicneVijestiKes[uuid] = slicne

                slicne.forEach { newsItem ->
                    NavigationState.syncNewsWithDatabase(newsItem)
                }

                return@withContext slicne
            }

            val original = trenutneSveVijesti.find { it.uuid == uuid }
                ?: throw InvalidUUIDException("News with UUID $uuid not found")

            val slicne = trenutneSveVijesti
                .filter { it.uuid != uuid && it.category == original.category }
                .take(2)
            slicneVijestiKes[uuid] = slicne

            return@withContext slicne

        } catch (e: Exception) {
            return@withContext NavigationState.getSimilarNewsHybrid(uuid, false)
        }
    }

    suspend fun getHeadlinesBySource(context: Context, sourceid: String): List<String> = withContext(Dispatchers.IO) {
        val hasInternet = NetworkUtils.hasInternetConnection(context)

        nasloviSaIstogIzvora[sourceid]?.let { return@withContext it }

        if (!hasInternet) {
            val headlines = trenutneSveVijesti
                .filter { it.source == sourceid }
                .map { it.title }
            if (headlines.isNotEmpty()) {
                nasloviSaIstogIzvora[sourceid] = headlines
                return@withContext headlines
            }
            throw Exception("No internet and no local data for source: $sourceid")
        }

        try {
            val response = api.getHeadlinesBySource(apiToken = API_KEY, sourceIds = sourceid)

            if (response.isSuccessful && response.body() != null) {
                val headlines = response.body()!!.data.map { it.title }
                nasloviSaIstogIzvora[sourceid] = headlines
                return@withContext headlines
            }

            throw Exception("Failed to fetch headlines for source: $sourceid")

        } catch (e: Exception) {
            val headlines = trenutneSveVijesti
                .filter { it.source == sourceid }
                .map { it.title }
            if (headlines.isNotEmpty()) {
                nasloviSaIstogIzvora[sourceid] = headlines
                return@withContext headlines
            }
            throw Exception("Fallback failed for source: $sourceid")
        }
    }
}
