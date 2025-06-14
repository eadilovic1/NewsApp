package etf.ri.rma.newsfeedapp.navigacija

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import etf.ri.rma.newsfeedapp.data.NewsDataWeb
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.concurrent.ConcurrentHashMap

object NavigationState {

    var trenutneSveVijesti: MutableList<NewsItem> = NewsDataWeb.getAllNews().toMutableList()

    var slicneVijestiKes: MutableMap<String, List<NewsItem>> = mutableMapOf()
        private set

    val tagoviSlikaKes = ConcurrentHashMap<String, List<String>>()

    var nasloviSaIstogIzvora = ConcurrentHashMap<String, List<String>>()
        private set

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
}