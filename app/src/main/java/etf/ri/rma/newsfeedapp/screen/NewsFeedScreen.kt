package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.extrastuff.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(navController: NavHostController = rememberNavController()) {

    var ucitavanje by remember { mutableStateOf(false) }
    var ucitajPrviPut by remember { mutableStateOf(true) }

    val pocetneVijesti = remember { NewsDAO.getAllNews() }

    var vijestiSaTagovimaSlika by remember { mutableStateOf(pocetneVijesti) }

    val scope = rememberCoroutineScope()

    val trenutnaKategorija = NavigationState.trenutnaKategorija ?: "All"
    val trenutniOpsegDatuma = NavigationState.trenutniOpsegDatuma
    val nepozeljneRijeci = NavigationState.nepozeljneRijeci

    suspend fun dohvatiVijestiZaKategoriju(kategorija: String): List<NewsItem> {
        val vijesti = if (kategorija == "All") NewsDAO.getAllNews()
        else NewsDAO.getTopStoriesByCategory(kategorija)
        return vijesti.map { vijest ->
            if (vijest.imageUrl != null && vijest.imageTags.isEmpty()) {
                try {
                    val tagovi = ImagaDAO.getTags(vijest.imageUrl)
                    vijest.copyByTags(tagovi)
                } catch (e: Exception) {
                    vijest
                }
            } else vijest
        }
    }

    LaunchedEffect(ucitajPrviPut) {
        if (ucitajPrviPut) {
            ucitavanje = true
            try {
                val vijesti = dohvatiVijestiZaKategoriju(trenutnaKategorija)
                if (vijesti.isNotEmpty()) {
                    vijestiSaTagovimaSlika = vijesti
                }
            } catch (_: Exception) {
            } finally {
                ucitavanje = false
                ucitajPrviPut = false
            }
        }
    }

    Column(modifier = Modifier.padding(top = 3.dp)) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(
                "All" to "filter_chip_all",
                "general" to "filter_chip_gen",
                "politics" to "filter_chip_pol",
                "sports" to "filter_chip_spo",
                "science" to "filter_chip_sci",
                "tech" to "filter_chip_tech",
                *if (trenutnaKategorija !in setOf("All", "general", "politics", "sports", "science", "tech")) {
                    arrayOf(trenutnaKategorija to "filter_chip_temp")
                } else emptyArray(),
                "More filters ..." to "filter_chip_more"
            ).forEach { (kategorija, oznaka) ->

                FilterChipComponent(
                    dodijeljenaKategorija = kategorija,
                    odabranaKategorija = trenutnaKategorija,
                    tag = oznaka,
                    onClick = { novaKategorija ->
                        if (oznaka == "filter_chip_more") {
                            navController.navigate("filters")
                        } else {
                            NavigationState.selectCategory(novaKategorija)
                            scope.launch {
                                ucitavanje = true
                                try {
                                    val noveVijesti = dohvatiVijestiZaKategoriju(novaKategorija)
                                    if (noveVijesti.isNotEmpty()) {
                                        vijestiSaTagovimaSlika = noveVijesti
                                    }
                                } catch (e: Exception) {
                                    println("Greška pri dohvaćanju vijesti: ${e.message}")
                                } finally {
                                    ucitavanje = false
                                }
                            }
                        }
                    }
                )
            }
        }

        val prikazaneVijesti = remember(
            trenutnaKategorija,
            trenutniOpsegDatuma,
            nepozeljneRijeci,
            vijestiSaTagovimaSlika
        ) {
            val filtriranoPoKategoriji = if (trenutnaKategorija == "All") {
                vijestiSaTagovimaSlika
            } else {
                vijestiSaTagovimaSlika.filter { it.category == trenutnaKategorija }
            }

            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            val filtriranoPoVremenu = trenutniOpsegDatuma?.let { (start, end) ->
                val startDate = runCatching { formatter.parse(start) }.getOrNull()
                val endDate = runCatching { formatter.parse(end) }.getOrNull()
                if (startDate != null && endDate != null) {
                    filtriranoPoKategoriji.filter { vijest ->
                        val datum = runCatching { formatter.parse(vijest.publishedDate) }.getOrNull()
                        datum != null && !datum.before(startDate) && !datum.after(endDate)
                    }
                } else filtriranoPoKategoriji
            } ?: filtriranoPoKategoriji

            if (nepozeljneRijeci.isEmpty()) {
                filtriranoPoVremenu
            } else {
                filtriranoPoVremenu.filter { vijest ->
                    nepozeljneRijeci.none { rijec ->
                        vijest.title.contains(rijec, ignoreCase = true) ||
                                vijest.snippet.contains(rijec, ignoreCase = true)
                    }
                }
            }
        }

        if (prikazaneVijesti.isNotEmpty()) {
            NewsList(listaVijesti = prikazaneVijesti, navController)
        } else {
            MessageCard(kategorija = trenutnaKategorija)
        }
    }
}