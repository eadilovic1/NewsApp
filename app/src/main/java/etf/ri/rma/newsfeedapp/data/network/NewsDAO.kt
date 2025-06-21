package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.network.api.NewsApiItem
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.nasloviSaIstogIzvora
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.slicneVijestiKes
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.trenutneSveVijesti
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.updateVrijemePoziva
import etf.ri.rma.newsfeedapp.navigacija.NavigationState.vrijemePozivaPoKategoriji
import etf.ri.rma.newsfeedapp.network.RetrofitClient
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

    // Dodajte companion object sa singleton instancom
    companion object {
        @Volatile
        private var INSTANCE: NewsDAO? = null

        // Dodaj za testove
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

        // Dodajte ove statičke metode za kompatibilnost sa postojećim kodom
        fun getAllNews(): List<NewsItem> = getInstance().getAllNews()

        suspend fun getTopStoriesByCategory(category: String): List<NewsItem> =
            getInstance().getTopStoriesByCategory(category)

        suspend fun getSimilarStories(uuid: String): List<NewsItem> =
            getInstance().getSimilarStories(uuid)

        suspend fun getHeadlinesBySource(sourceid: String): List<String> =
            getInstance().getHeadlinesBySource(sourceid)

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
            item.published_at // fallback to original if parsing fails
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

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        val kategorija = category.lowercase(Locale.ROOT)
        val trenutno = System.currentTimeMillis()
        val proslo = vrijemePozivaPoKategoriji[kategorija] ?: 0L
        val lokalne = trenutneSveVijesti.filter { it.category == kategorija }

        if (kategorija == "All") {
            return@withContext trenutneSveVijesti
        }

        if (trenutno - proslo < TimeUnit.SECONDS.toMillis(30)) {
            updateVrijemePoziva(kategorija)
            return@withContext lokalne
        }

        val odgovor = api.getTopStoriesByCategory(
            apiToken = API_KEY,
            categories = kategorija
        )

        val noveVijesti = if (odgovor.isSuccessful) {
            odgovor.body()?.data
                ?.map { mapApiItemToNewsItem(it, kategorija) }
                ?.filter { it.category == kategorija }
                ?.take(3)
        } else null

        if (noveVijesti != null) {
            // Filter out new items that already exist in the list (by uuid)
            val nepostojeci = noveVijesti.filter { nova -> trenutneSveVijesti.none { it.uuid == nova.uuid } }

            // Set old news to standard
            trenutneSveVijesti.replaceAll { vijest ->
                if (vijest.category == kategorija && nepostojeci.none { nova -> nova.uuid == vijest.uuid }) {
                    vijest.copyByType(false)
                } else vijest
            }
            // Add only truly new news as featured
            trenutneSveVijesti.addAll(0, nepostojeci.map { it.copyByType(true) })
        }

        updateVrijemePoziva(kategorija)

        return@withContext trenutneSveVijesti.filter { it.category == kategorija }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            UUID.fromString(uuid)
        } catch (e: Exception) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        if (slicneVijestiKes.containsKey(uuid)) {
            return@withContext slicneVijestiKes[uuid]!!
        }

        val response = api.getSimilarStories(
            uuid = uuid,
            apiToken = API_KEY
        )

        if (response.isSuccessful && response.body() != null) {
            val slicne = response.body()!!.data.map { mapApiItemToNewsItem(it) }.take(2)
            slicneVijestiKes[uuid] = slicne
            return@withContext slicne
        }

        val original = trenutneSveVijesti.find { it.uuid == uuid }
        if (original == null) {
            throw InvalidUUIDException("News with UUID $uuid not found")
        }

        val slicne = trenutneSveVijesti
            .filter { it.uuid != uuid && it.category == original.category }
            .take(2)
        slicneVijestiKes[uuid] = slicne

        return@withContext slicne
    }

    suspend fun getHeadlinesBySource(sourceid: String): List<String> = withContext(Dispatchers.IO) {
        nasloviSaIstogIzvora[sourceid]?.let {
            return@withContext it
        }

        val response = api.getHeadlinesBySource(
            apiToken = API_KEY,
            sourceIds = sourceid
        )

        if (response.isSuccessful && response.body() != null) {
            val headlines = response.body()!!.data.map { it.title }
            nasloviSaIstogIzvora[sourceid] = headlines
            return@withContext headlines
        }

        throw Exception("Failed to fetch headlines for source: $sourceid")
    }

}