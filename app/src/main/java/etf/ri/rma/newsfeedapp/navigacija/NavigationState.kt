package etf.ri.rma.newsfeedapp.navigacija

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import etf.ri.rma.newsfeedapp.data.NewsDataWeb
import etf.ri.rma.newsfeedapp.data.local.NewsDatabase
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

object NavigationState {
    private var database: NewsDatabase? = null
    var trenutneSveVijesti: MutableList<NewsItem> = mutableListOf()
    var slicneVijestiKes: MutableMap<String, List<NewsItem>> = mutableMapOf()
        private set
    val tagoviSlikaKes = ConcurrentHashMap<String, List<String>>()
    var nasloviSaIstogIzvora = ConcurrentHashMap<String, List<String>>()
        private set
    private val uuidToDbIdMap = ConcurrentHashMap<String, Int>()

    var trenutnaKategorija by mutableStateOf<String?>(null)
        private set
    var trenutnaVijestId by mutableStateOf<String?>(null)
        private set
    var trenutniOpsegDatuma by mutableStateOf<Pair<String, String>?>(null)
        private set
    var nepozeljneRijeci by mutableStateOf<List<String>>(emptyList())
        private set
    var vrijemePozivaPoKategoriji = mutableMapOf<String, Long>()
        private set

    fun initializeDatabase(context: Context) {
        try {
            database = NewsDatabase.getInstance(context.applicationContext)  // Koristite applicationContext

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Provjera da li baza radi
                    val test = database?.newsDao()?.loadAll()?.size ?: 0
                    Log.d("DB_TEST", "Baza uspješno inicijalizirana sa $test zapisa")

                    // Učitaj početne podatke ako je baza prazna
                    if (test == 0) {
                        NewsDataWeb.getAllNews().forEach { newsItem ->
                            syncNewsWithDatabase(newsItem)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DB_ERROR", "Greška pri testiranju baze", e)
                }
            }
        } catch (e: Exception) {
            Log.e("DB_INIT", "Kritična greška pri inicijalizaciji baze", e)
            // Ako baza ne radi, koristite samo memoriju
            trenutneSveVijesti.addAll(NewsDataWeb.getAllNews())
        }
    }

    private suspend fun loadNewsFromDatabase() {
        database?.let { db ->
            try {
                val savedNews = db.allNews()
                withContext(Dispatchers.Main) {
                    trenutneSveVijesti.clear()
                    trenutneSveVijesti.addAll(savedNews)

                    // Popuni mapu UUID -> DB ID
                    savedNews.forEach { newsItem ->
                        val dbId = findNewsIdByUuid(newsItem.uuid)
                        dbId?.let { uuidToDbIdMap[newsItem.uuid] = it }
                    }
                }
            } catch (e: Exception) {
                // Ako baza je prazna, učitaj početne podatke
                if (trenutneSveVijesti.isEmpty()) {
                    val initialNews = NewsDataWeb.getAllNews()
                    initialNews.forEach { newsItem ->
                        syncNewsWithDatabase(newsItem)
                    }
                }
            }
        }
    }

    fun initvrijemePozivaPoKategoriji() {
        val kategorije = listOf(
            "general", "science", "sports", "business", "health",
            "entertainment", "tech", "politics", "food", "travel"
        )
        vrijemePozivaPoKategoriji.clear()
        kategorije.forEach { kategorija ->
            vrijemePozivaPoKategoriji[kategorija] = 0L
        }
    }

    fun updateVrijemePoziva(kategorija: String) {
        vrijemePozivaPoKategoriji[kategorija] = System.currentTimeMillis()
    }

    fun selectCategory(category: String) {
        trenutnaKategorija = category
        trenutnaVijestId = null
    }

    fun selectNewsItem(newsItemId: String) {
        trenutnaVijestId = newsItemId
    }

    fun setFilters(category: String?, dateRange: Pair<String, String>?, unwantedWords: List<String>) {
        trenutnaKategorija = category
        trenutniOpsegDatuma = dateRange
        nepozeljneRijeci = unwantedWords
    }

    suspend fun saveNewsToDatabase(newsItem: NewsItem): Boolean {
        return database?.let { db ->
            try {
                val result = db.saveNews(newsItem)
                if (result) {
                    val allNews = db.allNews()
                    val savedItem = allNews.find { it.uuid == newsItem.uuid }
                    savedItem?.let {
                        val entities = db.newsDao().loadAll()
                        val entity = entities.find { it.uuid == newsItem.uuid }
                        entity?.let { e ->
                            uuidToDbIdMap[newsItem.uuid] = e.id
                        }
                    }
                }
                result
            } catch (e: Exception) {
                false
            }
        } ?: false
    }

    suspend fun getAllNewsFromDatabase(): List<NewsItem> {
        return database?.allNews() ?: emptyList()
    }

    suspend fun getNewsByCategoryFromDatabase(category: String): List<NewsItem> {
        return database?.getNewsWithCategory(category) ?: emptyList()
    }

    suspend fun addTagsToDatabase(tags: List<String>, newsId: Int): Int {
        return database?.addTags(tags, newsId) ?: 0
    }

    suspend fun getTagsFromDatabase(newsId: Int): List<String> {
        return database?.getTags(newsId) ?: emptyList()
    }

    suspend fun getSimilarNewsFromDatabase(tags: List<String>): List<NewsItem> {
        return database?.getSimilarNews(tags) ?: emptyList()
    }

    suspend fun findNewsIdByUuid(uuid: String): Int? {
        uuidToDbIdMap[uuid]?.let { return it }

        return database?.let { db ->
            try {
                val entities = db.newsDao().loadAll()
                val entity = entities.find { it.uuid == uuid }
                entity?.let {
                    uuidToDbIdMap[uuid] = it.id
                    it.id
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getNewsHybrid(category: String, hasInternet: Boolean): List<NewsItem> {
        return if (hasInternet) {
            trenutneSveVijesti.filter {
                if (category == "All") true else it.category == category
            }
        } else {
            if (category == "All") {
                getAllNewsFromDatabase()
            } else {
                getNewsByCategoryFromDatabase(category)
            }
        }
    }

    suspend fun getSimilarNewsHybrid(uuid: String, hasInternet: Boolean): List<NewsItem> {
        return if (hasInternet) {
            slicneVijestiKes[uuid] ?: emptyList()
        } else {
            findNewsIdByUuid(uuid)?.let { newsId ->
                val tags = getTagsFromDatabase(newsId)
                getSimilarNewsFromDatabase(tags)
            } ?: emptyList()
        }
    }

    suspend fun getTagsHybrid(uuid: String, hasInternet: Boolean): List<String> {
        return if (hasInternet) {
            trenutneSveVijesti.find { it.uuid == uuid }?.imageTags ?: emptyList()
        } else {
            findNewsIdByUuid(uuid)?.let { newsId ->
                getTagsFromDatabase(newsId)
            } ?: emptyList()
        }
    }

    suspend fun syncNewsWithDatabase(newsItem: NewsItem) {
        saveNewsToDatabase(newsItem)
        if (trenutneSveVijesti.none { it.uuid == newsItem.uuid }) {
            trenutneSveVijesti.add(newsItem)
        }
    }

    suspend fun syncTagsWithDatabase(uuid: String, tags: List<String>) {
        findNewsIdByUuid(uuid)?.let { newsId ->
            addTagsToDatabase(tags, newsId)
        }

        val index = trenutneSveVijesti.indexOfFirst { it.uuid == uuid }
        if (index != -1) {
            trenutneSveVijesti[index] = trenutneSveVijesti[index].copyByTags(tags)
        }
    }
}