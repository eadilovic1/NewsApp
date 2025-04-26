package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import etf.ri.rma.newsfeedapp.FilterChipComponent
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavHostController) {
    val sveVijesti = remember { NewsData.getAllNews() }
    var privremenaKategorija by remember { mutableStateOf(NavigationState.trenutnaKategorija ?: "Sve") }

    // State za nepoželjne riječi
    var novaRijec by remember { mutableStateOf("") }
    val privremeneNepozeljneRijeci = remember {
        mutableStateListOf<String>().apply { addAll(NavigationState.nepozeljneRijeci) }
    }

    // State za date range picker
    var prikaziDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayMode = DisplayMode.Picker
    )
    var odabraniDatumi by remember { mutableStateOf(
        NavigationState.trenutniOpsegDatuma?.let { "${it.first};${it.second}" }
    )}

    // Formatiranje datuma za prikaz
    fun formatirajDatum(datum: Long): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(Date(datum))
    }

    // Automatsko ažuriranje odabranog datuma
    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        dateRangePickerState.selectedStartDateMillis?.let { pocetniDatum ->
            dateRangePickerState.selectedEndDateMillis?.let { krajnjiDatum ->
                odabraniDatumi = "${formatirajDatum(pocetniDatum)};${formatirajDatum(krajnjiDatum)}"
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        // Sekcija za filtriranje po kategorijama
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
            ).forEach { (kategorija, oznaka) ->
                FilterChipComponent(
                    dodijeljenaKategorija = kategorija,
                    odabranaKategorija = privremenaKategorija,
                    onClick = { odabrana -> privremenaKategorija = odabrana },
                    tag = oznaka
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sekcija za filtriranje po datumu
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Filtriraj po vremenskom rasponu",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = odabraniDatumi ?: "Odaberite vremenski raspon",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_daterange_display"),
                        textAlign = TextAlign.Start
                    )

                    Button(
                        onClick = { prikaziDateRangePicker = true },
                        modifier = Modifier.testTag("filter_daterange_button")
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Odabir datuma")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Odaberi")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sekcija za filtriranje po nepoželjnim riječima
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Filtriraj po nepoželjnim riječima",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = novaRijec,
                        onValueChange = { novaRijec = it },
                        label = { Text("Unesite nepoželjnu riječ") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_unwanted_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val trimmedRijec = novaRijec.trim()
                            if (trimmedRijec.isNotEmpty() &&
                                !privremeneNepozeljneRijeci.any { it.equals(trimmedRijec, ignoreCase = true) }
                            ) {
                                privremeneNepozeljneRijeci.add(trimmedRijec)
                                novaRijec = ""
                            }
                        },
                        modifier = Modifier.testTag("filter_unwanted_add_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj riječ")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (privremeneNepozeljneRijeci.isNotEmpty()) {
                    Text(
                        text = "Lista nepoželjnih riječi:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("filter_unwanted_list")
                    ) {
                        itemsIndexed(privremeneNepozeljneRijeci) { index, rijec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = rijec,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { privremeneNepozeljneRijeci.removeAt(index) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Ukloni riječ"
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // Dugme "Primijeni filtere" na dnu ekrana
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    NavigationState.setFilters(
                        category = privremenaKategorija,
                        dateRange = odabraniDatumi?.split(";")?.let { Pair(it[0], it[1]) },
                        unwantedWords = privremeneNepozeljneRijeci
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("filter_apply_button")
            ) {
                Text("Primijeni filtere")
            }
        }

        // Dijalog za odabir datuma
        if (prikaziDateRangePicker) {
            androidx.compose.material3.DatePickerDialog(
                onDismissRequest = { prikaziDateRangePicker = false },
                confirmButton = {
                    Button(onClick = { prikaziDateRangePicker = false }) {
                        Text("Potvrdi")
                    }
                },
                dismissButton = {
                    Button(onClick = { prikaziDateRangePicker = false }) {
                        Text("Odustani")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier
                        .height(500.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}