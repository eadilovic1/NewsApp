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
import etf.ri.rma.newsfeedapp.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.NewsData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(){
    val sveVijesti = remember { NewsData.getAllNews() }
    var trenutnaKategorija by remember { mutableStateOf("Sve") }
    val prikazaneVijesti = remember(trenutnaKategorija) {
        if (trenutnaKategorija == "Sve") sveVijesti
        else sveVijesti.filter { it.category == trenutnaKategorija }
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
                "Prazna kategorija" to "filter_chip_none"
            ).forEach { (kategorija, oznaka) ->
                FilterChipComponent(
                    dodijeljenaKategorija = kategorija,
                    odabranaKategorija = trenutnaKategorija,
                    onClick = { odabrana -> trenutnaKategorija = odabrana },
                    tag = oznaka
                )
            }
        }


        if (prikazaneVijesti.isNotEmpty()) {
            NewsList(listaVijesti = prikazaneVijesti)
        } else {
            MessageCard(kategorija = trenutnaKategorija)
        }
    }
}