package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import etf.ri.rma.newsfeedapp.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(navController: NavHostController) {
    val sveVijesti = remember { NewsData.getAllNews() }

    // Dohvaćamo sve filtere iz NavigationState
    val trenutnaKategorija = NavigationState.trenutnaKategorija ?: "Sve"
    val trenutniOpsegDatuma = NavigationState.trenutniOpsegDatuma
    val nepozeljneRijeci = NavigationState.nepozeljneRijeci

    // Filtriramo vijesti prema svim filterima
    val prikazaneVijesti = remember(trenutnaKategorija, trenutniOpsegDatuma, nepozeljneRijeci) {
        // Korak 1: Filtriranje po kategoriji
        val filtriranoPoKategoriji = if (trenutnaKategorija == "Sve") {
            sveVijesti
        } else {
            sveVijesti.filter { it.category == trenutnaKategorija }
        }

        // Korak 2: Filtriranje po datumima
        val filtriranoPoVremenu = if (trenutniOpsegDatuma == null) {
            filtriranoPoKategoriji
        } else {
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val pocetniDatum = formatter.parse(trenutniOpsegDatuma.first)
            val krajnjiDatum = formatter.parse(trenutniOpsegDatuma.second)

            filtriranoPoKategoriji.filter { vijest ->
                val datumVijesti = formatter.parse(vijest.publishedDate)
                datumVijesti != null && !datumVijesti.before(pocetniDatum) && !datumVijesti.after(krajnjiDatum)
            }
        }

        // Korak 3: Filtriranje po nepoželjnim riječima
        if (nepozeljneRijeci.isEmpty()) {
            filtriranoPoVremenu
        } else {
            filtriranoPoVremenu.filter { vijest ->
                // Vijest se prikazuje ako ne sadrži niti jednu nepoželjnu riječ
                nepozeljneRijeci.none { rijec ->
                    vijest.title.contains(rijec, ignoreCase = true) ||
                            vijest.snippet.contains(rijec, ignoreCase = true)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            listOf(
                "Politika" to "filter_chip_pol",
                "Sport" to "filter_chip_spo",
                "Nauka/Tehnologija" to "filter_chip_sci",
                "Sve" to "filter_chip_all",
                "Prazna kategorija" to "filter_chip_none",
                "Više filtera..." to "filter_chip_more"
            ).forEach { (kategorija, oznaka) ->
                if(oznaka == "filter_chip_more"){
                    FilterChipComponent(
                        dodijeljenaKategorija = kategorija,
                        odabranaKategorija = "",
                        onClick = { navController.navigate("filters") },
                        tag = oznaka
                    )
                }
                else{
                    FilterChipComponent(
                        dodijeljenaKategorija = kategorija,
                        odabranaKategorija = trenutnaKategorija,
                        onClick = { odabrana -> NavigationState.selectCategory(odabrana) },
                        tag = oznaka
                    )
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