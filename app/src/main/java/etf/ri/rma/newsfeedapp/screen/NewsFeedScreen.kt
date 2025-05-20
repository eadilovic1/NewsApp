package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(navController: NavHostController = rememberNavController()) {
    val sveVijesti = remember { NewsData.getAllNews() }

    val trenutnaKategorija = NavigationState.trenutnaKategorija ?: "Sve"
    val trenutniOpsegDatuma = NavigationState.trenutniOpsegDatuma
    val nepozeljneRijeci = NavigationState.nepozeljneRijeci

    val prikazaneVijesti = remember(trenutnaKategorija, trenutniOpsegDatuma, nepozeljneRijeci) {
        val filtriranoPoKategoriji = if (trenutnaKategorija == "Sve") {
            sveVijesti
        } else {
            sveVijesti.filter { it.category == trenutnaKategorija }
        }

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val filtriranoPoVremenu = if (trenutniOpsegDatuma != null) {
            val pocetniDatum = try { formatter.parse(trenutniOpsegDatuma.first) } catch (e: Exception) { null }
            val krajnjiDatum = try { formatter.parse(trenutniOpsegDatuma.second) } catch (e: Exception) { null }

            if (pocetniDatum != null && krajnjiDatum != null) {
                filtriranoPoKategoriji.filter { vijest ->
                    val datumVijesti = try { formatter.parse(vijest.publishedDate) } catch (e: Exception) { null }
                    datumVijesti != null &&
                            !datumVijesti.before(pocetniDatum) &&
                            !datumVijesti.after(krajnjiDatum)
                }
            } else filtriranoPoKategoriji
        } else filtriranoPoKategoriji

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

    Column(
        modifier = Modifier.padding(top = 3.dp)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(
                "Politika" to "filter_chip_pol",
                "Sport" to "filter_chip_spo",
                "Nauka/Tehnologija" to "filter_chip_sci",
                "Sve" to "filter_chip_all",
                "Prazna kategorija" to "filter_chip_none",
                "Više filtera ..." to "filter_chip_more"
            ).forEach { (kategorija, oznaka) ->
                if (oznaka == "filter_chip_more") {
                    FilterChipComponent(
                        dodijeljenaKategorija = kategorija,
                        odabranaKategorija = "",
                        onClick = { navController.navigate("filters") },
                        tag = oznaka
                    )
                } else {
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