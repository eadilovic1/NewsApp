package etf.ri.rma.newsfeedapp.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.extrastuff.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.extrastuff.NetworkUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    var ucitavanje by remember { mutableStateOf(false) }
    var ucitajPrviPut by remember { mutableStateOf(true) }

    val pocetneVijesti = remember { NewsDAO.getAllNews() }

    var vijestiSaTagovimaSlika by remember { mutableStateOf(pocetneVijesti) }

    val scope = rememberCoroutineScope()

    val trenutnaKategorija = NavigationState.trenutnaKategorija ?: "All"
    val trenutniOpsegDatuma = NavigationState.trenutniOpsegDatuma
    val nepozeljneRijeci = NavigationState.nepozeljneRijeci

    suspend fun dohvatiVijestiZaKategoriju(context: Context, kategorija: String): List<NewsItem> {
        val hasInternet = NetworkUtils.hasInternetConnection(context)

        val vijesti = if (kategorija == "All") {
            if (hasInternet) NewsDAO.getAllNews()
            else NavigationState.getAllNewsFromDatabase()
        } else {
            NewsDAO.getTopStoriesByCategory(context, kategorija)
        }

        return vijesti.map { vijest ->
            if (vijest.imageUrl != null && vijest.imageTags.isEmpty()) {
                try {
                    val tagovi = if (hasInternet) {
                        ImagaDAO.getTagsForNewsItem(context, vijest.uuid, vijest.imageUrl)
                    } else {
                        NavigationState.getTagsHybrid(vijest.uuid, false)
                    }

                    val updatedVijest = vijest.copyByTags(tagovi)

                    if (tagovi.isNotEmpty()) {
                        NavigationState.syncNewsWithDatabase(updatedVijest)
                        if (hasInternet) {
                            NavigationState.syncTagsWithDatabase(vijest.uuid, tagovi)
                        }
                    }

                    updatedVijest
                } catch (e: Exception) {
                    if (!hasInternet) {
                        val tagsFromDb = NavigationState.getTagsHybrid(vijest.uuid, false)
                        if (tagsFromDb.isNotEmpty()) {
                            vijest.copyByTags(tagsFromDb)
                        } else vijest
                    } else vijest
                }
            } else vijest
        }
    }

    LaunchedEffect(ucitajPrviPut) {
        if (ucitajPrviPut) {
            ucitavanje = true
            try {
                val vijesti = dohvatiVijestiZaKategoriju(context, trenutnaKategorija)
                if (vijesti.isNotEmpty()) {
                    vijestiSaTagovimaSlika = vijesti
                }
            } catch (_: Exception) {
                try {
                    val vijestiIzBaze = if (trenutnaKategorija == "All") {
                        NavigationState.getAllNewsFromDatabase()
                    } else {
                        NavigationState.getNewsByCategoryFromDatabase(trenutnaKategorija)
                    }
                    if (vijestiIzBaze.isNotEmpty()) {
                        vijestiSaTagovimaSlika = vijestiIzBaze
                    }
                } catch (_: Exception) {}
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
                                    val noveVijesti = dohvatiVijestiZaKategoriju(context, novaKategorija)
                                    if (noveVijesti.isNotEmpty()) {
                                        vijestiSaTagovimaSlika = noveVijesti
                                    }
                                } catch (e: Exception) {
                                    try {
                                        val vijestiIzBaze = if (novaKategorija == "All") {
                                            NavigationState.getAllNewsFromDatabase()
                                        } else {
                                            NavigationState.getNewsByCategoryFromDatabase(novaKategorija)
                                        }
                                        if (vijestiIzBaze.isNotEmpty()) {
                                            vijestiSaTagovimaSlika = vijestiIzBaze
                                        }
                                    } catch (dbException: Exception) {
                                        println("Greška pri dohvaćanju vijesti iz baze: ${dbException.message}")
                                    }
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