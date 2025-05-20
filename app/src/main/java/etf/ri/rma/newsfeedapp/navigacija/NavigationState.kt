package etf.ri.rma.newsfeedapp.navigacija

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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

    fun setFilters(category: String?, dateRange: Pair<String, String>?, unwantedWords: List<String>) {
        trenutnaKategorija = category
        trenutniOpsegDatuma = dateRange
        nepozeljneRijeci = unwantedWords
    }
}