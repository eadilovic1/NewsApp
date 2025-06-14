package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch

@Composable
fun NewsDetailsScreen(navController: NavHostController, id: String) {
    val listaVijesti = remember { mutableStateListOf<NewsItem>().apply { addAll(NewsDAO.getAllNews()) } }
    val trenutnaVijest = remember(id) { listaVijesti.find { it.uuid == id } }

    var slicneVijesti by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var headlinesBySource by remember { mutableStateOf<List<String>>(emptyList()) }

    var tagoviSlika by remember { mutableStateOf<List<String>>(emptyList()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        trenutnaVijest?.let { vijest ->
            scope.launch {
                runCatching {
                    NewsDAO.getSimilarStories(vijest.uuid)
                }.onSuccess { slicne ->
                    slicneVijesti = slicne
                    slicne.forEach { item ->
                        NewsDAO.addNewsItem(item, vijest.category)
                    }
                }.onFailure { e ->
                    println("Greška pri dohvatanju sličnih vijesti: ${e.message}")
                }

                vijest.imageUrl?.let { url ->
                    runCatching {
                        ImagaDAO.getTags(url)
                    }.onSuccess { tagovi ->
                        tagoviSlika = tagovi
                        val index = listaVijesti.indexOfFirst { it.uuid == vijest.uuid }
                        if (index != -1) {
                            val novaVijest = listaVijesti[index].copyByTags(tagovi)
                            listaVijesti[index] = novaVijest
                        }
                    }.onFailure { e ->
                        println("Greška pri tagovanju slike: ${e.message}")
                    }
                }

                runCatching {
                    NewsDAO.getHeadlinesBySource(vijest.source)
                }.onSuccess { headlines ->
                    headlinesBySource = headlines
                }.onFailure { e ->
                    println("Greška pri dohvatanju naslova iz istog izvora: ${e.message}")
                }
            }
        }
    }

    BackHandler {
        navController.popBackStack("home", inclusive = false)
    }

    if (trenutnaVijest == null) {
        Text("Vijest nije pronađena.")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = 72.dp)
        ) {
            if (trenutnaVijest.imageUrl != null){
                AsyncImage(
                    model = trenutnaVijest.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tagovi slike",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("deatils_image_tags"),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (tagoviSlika.isEmpty()) {
                Text(
                    text = "Nema tagova"
                )
            }
            else{
                Text(
                    text = tagoviSlika.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Naslovi sa istog izvora",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("titles_from_same_source"),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (headlinesBySource.isEmpty()) {
                Text(
                    text = "Nema više vijesti na ovom izvoru",
                )
            }
            else{
                Text(
                    text = headlinesBySource.joinToString("\n"),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Povezane vijesti iz iste kategorije",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            slicneVijesti.forEachIndexed { index, vijest ->
                Text(
                    text = vijest.title,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("details/${vijest.uuid}")
                        }
                        .testTag("related_news_title_${index + 1}")
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ){
            Button(
                onClick = {
                    navController.popBackStack("home", inclusive = false)
                },
                modifier = Modifier.testTag("details_close_button")
            ) {
                Text(
                    text = "Zatvori detalje"
                )
            }
        }
    }
}