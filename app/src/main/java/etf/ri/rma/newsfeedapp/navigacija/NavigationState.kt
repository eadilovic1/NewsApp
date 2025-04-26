package etf.ri.rma.newsfeedapp.navigacija

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem

object NavigationState {

    var trenutnaKategorija by mutableStateOf<String?>(null)
        private set

    var trenutnaVijestId by mutableStateOf<String?>(null)
        private set

    var trenutniOpsegDatuma by mutableStateOf<Pair<String, String>?>(null)
        private set

    var nepozeljneRijeci by mutableStateOf<List<String>>(emptyList())
        private set

    fun selectCategory(category: String) {
        trenutnaKategorija = category
        trenutnaVijestId = null
    }

    fun selectNewsItem(newsItemId: String) {
        trenutnaVijestId = newsItemId
    }

    fun getTrenutnaVijest(): NewsItem? {
        return NewsData.getAllNews().find { it.id == trenutnaVijestId }
    }

    fun backToNewsList() {
        trenutnaVijestId = null
    }

    fun setDateRange(startDate: String, endDate: String) {
        trenutniOpsegDatuma = Pair(startDate, endDate)
    }

    fun addUnwantedWord(word: String) {
        if (word.isNotBlank() && nepozeljneRijeci.none { it.equals(word, ignoreCase = true) }) {
            nepozeljneRijeci = nepozeljneRijeci + word
        }
    }

    fun clearFilters() {
        trenutniOpsegDatuma = null
        nepozeljneRijeci = emptyList()
    }

    fun setFilters(category: String?, dateRange: Pair<String, String>?, unwantedWords: List<String>) {
        trenutnaKategorija = category
        trenutniOpsegDatuma = dateRange
        nepozeljneRijeci = unwantedWords
    }
}
