package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewsDetailsScreen(navController: NavHostController, id : String) {
    val listaVijesti = remember { NewsData.getAllNews() }
    val trenutnaVijest = remember(id) { listaVijesti.find { it.id == id } }

    // Funkcija za povratak na glavni ekran
    fun backToHome() {
        NavigationState.backToNewsList()
        navController.navigate("home") {
            // Briše prethodni ekran detalja iz back stack-a
            popUpTo("home") {
                inclusive = false
            }
        }
    }

    // Handling sistemskog back dugmeta
    BackHandler {
        backToHome()
    }

    if (trenutnaVijest == null) {
        Text("Vijest nije pronađena.")
        return
    }

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val relatedNews = remember(trenutnaVijest) {
        listaVijesti.filter { it.id != trenutnaVijest.id && it.category == trenutnaVijest.category }
            .sortedWith(
                compareBy(
                    {
                        try {
                            val d1 = dateFormat.parse(it.publishedDate)
                            val d2 = dateFormat.parse(trenutnaVijest.publishedDate)
                            kotlin.math.abs(d1!!.time - d2!!.time)
                        } catch (e: Exception) {
                            0L // Ako je parsiranje neuspješno, koristimo 0 kao default vrijednost
                        }
                    },
                    { it.title }
                )
            )
            .take(2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = trenutnaVijest.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("details_title")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = trenutnaVijest.snippet,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("details_snippet")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kategorija: ${trenutnaVijest.category}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_category")
        )

        Text(
            text = "Izvor: ${trenutnaVijest.source}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_source")
        )

        Text(
            text = "Datum: ${trenutnaVijest.publishedDate}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_date")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Povezane vijesti iz iste kategorije",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        relatedNews.forEachIndexed { index, vijest ->
            Text(
                text = vijest.title,
                modifier = Modifier
                    .clickable {
                        NavigationState.selectNewsItem(vijest.id)
                        navController.navigate("details/${vijest.id}")  // Dodano za navigaciju na povezane vijesti
                    }
                    .testTag("related_news_title_${index + 1}")
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                backToHome()
            },
            modifier = Modifier.testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}