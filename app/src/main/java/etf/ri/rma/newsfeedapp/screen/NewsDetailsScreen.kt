package etf.ri.rma.newsfeedapp.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.ui.theme.NewsAppTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewsDetailsScreen(navController: NavHostController) {
    val listaVijesti = remember { NewsData.getAllNews() }
    //PROVJERITI KAKO JE INICIJALIZOVAN currentId
    val currentId = navController.currentBackStackEntry?.arguments?.getString("id")
    
    if (currentId == null) {
        Text("Vijest nije pronađena.")
        return
    }
    val trenutnaVijest = remember {
        NavigationState.getTrenutnaVijest()
    } ?: run {
        Text("Vijest nije pronađena.")
        return
    }

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val relatedNews = remember(trenutnaVijest) {
        listaVijesti.filter { it.id != trenutnaVijest.id && it.category == trenutnaVijest.category }
            .sortedWith(
                compareBy(
                    {
                        val d1 = dateFormat.parse(it.publishedDate)
                        val d2 = dateFormat.parse(trenutnaVijest.publishedDate)
                        kotlin.math.abs(d1!!.time - d2!!.time)
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
            text = "Datum: ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(trenutnaVijest.publishedDate)}",
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
                    }
                    .testTag("related_news_title_${index + 1}")
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                NavigationState.backToNewsList()
                navController.navigate("home")
            },
            modifier = Modifier.testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewsDetailsScreenPreview() {
    NewsAppTheme(isDarkTheme = false) { // Zamijenite sa vašom temom ako je imate
        val navController = rememberNavController()

        // Kreirajte testni NewsItem
        val testNews = NewsItem(
            id = "1",
            title = "Testna vijest",
            snippet = "Ovo je kratak opis testne vijesti koja sadrži neke detalje.",
            category = "Tehnologija",
            imageUrl = null,
            isFeatured = true,
            source = "Testni izvor",
            publishedDate = "01-01-2023"
        )

        // Postavite testne argumente
        navController.currentBackStackEntry?.arguments?.putString("id", testNews.id)

        NewsDetailsScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "No News Found")
@Composable
fun NewsDetailsScreenNotFoundPreview() {
    NewsAppTheme(isDarkTheme = true) {
        val navController = rememberNavController()
        // Ne postavljamo argument "id" kako bismo simulirali nepronađenu vijest
        NewsDetailsScreen(navController = navController)
    }
}