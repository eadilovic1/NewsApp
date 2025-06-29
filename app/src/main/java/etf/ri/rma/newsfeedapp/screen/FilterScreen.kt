package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.extrastuff.FilterChipComponent
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavHostController = rememberNavController()) {
    var privremenaKategorija by remember { mutableStateOf(NavigationState.trenutnaKategorija ?: "All") }

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

    // Inicijalno stanje datuma koje se koristi za povratak na prethodno stanje
    val inicijalniDatumi = remember {
        NavigationState.trenutniOpsegDatuma?.let { "${it.first};${it.second}" }
    }

    // Trenutni odabrani datumi
    var odabraniDatumi by remember { mutableStateOf(inicijalniDatumi) }

    // Privremeni odabrani datumi koji se koriste dok je dijalog otvoren
    var privremeniOdabraniDatumi by remember { mutableStateOf(odabraniDatumi) }

    // Formatiranje datuma za prikaz
    fun formatirajDatum(datum: Long): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(Date(datum))
    }

    // Postavljanje inicijalnog stanja picker-a kada se dijalog otvori
    fun postaviDatePickerState() {
        if (odabraniDatumi != null) {
            val dijelovi = odabraniDatumi!!.split(";")
            if (dijelovi.size == 2) {
                try {
                    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val pocetniDatum = format.parse(dijelovi[0])?.time
                    val krajnjiDatum = format.parse(dijelovi[1])?.time

                    dateRangePickerState.setSelection(
                        startDateMillis = pocetniDatum,
                        endDateMillis = krajnjiDatum
                    )
                } catch (e: Exception) {
                    // Ako ne može da parsira datume, ne radi ništa
                }
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
                "All" to "filter_chip_all",
                "general" to "filter_chip_gen",
                "science" to "filter_chip_sci",
                "sports" to "filter_chip_spo",
                "business" to "filter_chip_bus",
                "health" to "filter_chip_hea",
                "entertainment" to "filter_chip_ent",
                "tech" to "filter_chip_tech",
                "politics" to "filter_chip_pol",
                "food" to "filter_chip_food",
                "travel" to "filter_chip_tra"
            ).forEach { (kategorija, oznaka) ->
                FilterChipComponent(
                    dodijeljenaKategorija = kategorija,
                    odabranaKategorija = privremenaKategorija,
                    tag = oznaka,
                    onClick = { odabrana -> privremenaKategorija = odabrana }
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
                        onClick = {
                            postaviDatePickerState()
                            privremeniOdabraniDatumi = odabraniDatumi
                            prikaziDateRangePicker = true
                        },
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

        // Potrebno da dugmad Odustani/Primjijeni filtere budu fiksno na dnu ekrana
        Spacer(modifier = Modifier.weight(1f))

        // Dugmad Odustani/Primjijeni filtere
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    navController.navigate("home")
                },
                modifier = Modifier.height(50.dp)
            ) {
                Text("Odustani")
            }

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
                    .height(50.dp)
                    .testTag("filter_apply_button")
            ) {
                Text("Primijeni filtere")
            }
        }

        // Dijalog za odabir datuma
        if (prikaziDateRangePicker) {
            // Praćenje promjena odabranih datuma
            LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
                dateRangePickerState.selectedStartDateMillis?.let { pocetniDatum ->
                    dateRangePickerState.selectedEndDateMillis?.let { krajnjiDatum ->
                        privremeniOdabraniDatumi = "${formatirajDatum(pocetniDatum)};${formatirajDatum(krajnjiDatum)}"
                    }
                }
            }

            androidx.compose.material3.DatePickerDialog(
                onDismissRequest = {
                    // Zatvaranje dijaloga bez promjene
                    prikaziDateRangePicker = false
                },
                confirmButton = {
                    Button(onClick = {
                        // Potvrdi odabir datuma
                        odabraniDatumi = privremeniOdabraniDatumi
                        prikaziDateRangePicker = false
                    }) {
                        Text("Potvrdi")
                    }
                },
                dismissButton = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                // Poništi odabir datuma (potpuno)
                                odabraniDatumi = null
                                privremeniOdabraniDatumi = null
                                dateRangePickerState.setSelection(null, null)
                                prikaziDateRangePicker = false
                            }
                        ) {
                            Text("Poništi")
                        }

                        Button(onClick = {
                            // Odustani od odabira datuma - ne mijenja odabraniDatumi
                            prikaziDateRangePicker = false
                        }) {
                            Text("Odustani")
                        }
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