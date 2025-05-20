package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsList(listaVijesti: List<NewsItem>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list"),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(listaVijesti) { vijest ->
            if (vijest.isFeatured) {
                FeaturedNewsCard(vijest, navController)
            } else {
                StandardNewsCard(vijest, navController)
            }
        }
    }
}