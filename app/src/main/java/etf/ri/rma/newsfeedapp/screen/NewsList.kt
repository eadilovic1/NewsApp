package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.ui.theme.LemonChiffon

@Composable
fun NewsList(listaVijesti: List<NewsItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(listaVijesti) { vijest ->
            if (vijest.isFeatured) {
                FeaturedNewsCard(vijest)
            } else {
                StandardNewsCard(vijest)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewsListPreview() {
    val dummyNews = listOf(
        NewsItem(
            id = "1",
            title = "Naslov 1 je puno duži od drugog naslova kako bismo testirali prelazak u novi red",
            snippet = "Ovo je kratki opis vijesti broj 1.",
            imageUrl = "",
            source = "Klix.ba",
            publishedDate = "2025-04-08",
            category = "Sport",
            isFeatured = false
        ),
        NewsItem(
            id = "2",
            title = "Naslov 2",
            snippet = "Vijest broj 2 ima malo duži opis da testiramo izgled pri prelasku u novi red.",
            imageUrl = "",
            source = "Al Jazeera",
            publishedDate = "2025-04-07",
            category = "Politika",
            isFeatured = true
        )
    )
    NewsList(listaVijesti = dummyNews)
}