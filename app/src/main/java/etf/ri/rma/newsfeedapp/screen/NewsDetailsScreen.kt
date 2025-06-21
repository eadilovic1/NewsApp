package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.extrastuff.NetworkUtils
import kotlinx.coroutines.launch

@Composable
fun NewsDetailsScreen(navController: NavHostController, id: String) {
    val context = LocalContext.current
    val listaVijesti = remember { mutableStateListOf<NewsItem>().apply { addAll(NewsDAO.getAllNews()) } }
    val trenutnaVijest = remember(id) { listaVijesti.find { it.uuid == id } }

    var slicneVijesti by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var headlinesBySource by remember { mutableStateOf<List<String>>(emptyList()) }
    var tagoviSlika by remember { mutableStateOf<List<String>>(emptyList()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        trenutnaVijest?.let { vijest ->
            val hasInternet = NetworkUtils.hasInternetConnection(context)

            scope.launch {
                // Dohvati slične vijesti
                runCatching {
                    if (hasInternet) {
                        NewsDAO.getSimilarStories(context, vijest.uuid)
                    } else {
                        NavigationState.getSimilarNewsHybrid(vijest.uuid, false)
                    }
                }.onSuccess { slicne ->
                    slicneVijesti = slicne
                    slicne.forEach { item ->
                        if (hasInternet) {
                            NewsDAO.addNewsItem(item, vijest.category)
                            NavigationState.syncNewsWithDatabase(item)
                        }
                    }
                }.onFailure {
                    if (!hasInternet) {
                        slicneVijesti = NavigationState.getSimilarNewsHybrid(vijest.uuid, false)
                    }
                }

                // Dohvati tagove slike
                vijest.imageUrl?.let { url ->
                    runCatching {
                        if (hasInternet) {
                            ImagaDAO.getTagsForNewsItem(context, vijest.uuid, url)
                        } else {
                            NavigationState.getTagsHybrid(vijest.uuid, false)
                        }
                    }.onSuccess { tagovi ->
                        tagoviSlika = tagovi
                        val index = listaVijesti.indexOfFirst { it.uuid == vijest.uuid }
                        if (index != -1) {
                            val novaVijest = listaVijesti[index].copyByTags(tagovi)
                            listaVijesti[index] = novaVijest

                            if (tagovi.isNotEmpty()) {
                                NavigationState.syncNewsWithDatabase(novaVijest)
                                if (hasInternet) {
                                    NavigationState.syncTagsWithDatabase(vijest.uuid, tagovi)
                                }
                            }
                        }
                    }.onFailure {
                        if (!hasInternet) {
                            tagoviSlika = NavigationState.getTagsHybrid(vijest.uuid, false)
                        }
                    }
                }

                // Dohvati naslove iz istog izvora
                runCatching {
                    if (hasInternet) {
                        NewsDAO.getHeadlinesBySource(context, vijest.source)
                    } else {
                        val localHeadlines = NavigationState.getAllNewsFromDatabase()
                            .filter { it.source == vijest.source }
                            .map { it.title }
                        localHeadlines.ifEmpty {
                            NavigationState.trenutneSveVijesti
                                .filter { it.source == vijest.source }
                                .map { it.title }
                        }
                    }
                }.onSuccess { headlines ->
                    headlinesBySource = headlines
                }.onFailure {
                    headlinesBySource = NavigationState.trenutneSveVijesti
                        .filter { it.source == vijest.source }
                        .map { it.title }
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
            if (trenutnaVijest.imageUrl != null) {
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

            Text("Kategorija: ${trenutnaVijest.category}", modifier = Modifier.testTag("details_category"))
            Text("Izvor: ${trenutnaVijest.source}", modifier = Modifier.testTag("details_source"))
            Text("Datum: ${trenutnaVijest.publishedDate}", modifier = Modifier.testTag("details_date"))

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tagovi slike", fontWeight = FontWeight.SemiBold, modifier = Modifier.testTag("deatils_image_tags"))
            Spacer(modifier = Modifier.height(4.dp))
            if (tagoviSlika.isEmpty()) {
                Text("Nema tagova")
            } else {
                Text(tagoviSlika.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Naslovi sa istog izvora", fontWeight = FontWeight.SemiBold, modifier = Modifier.testTag("titles_from_same_source"))
            Spacer(modifier = Modifier.height(4.dp))
            if (headlinesBySource.isEmpty()) {
                Text("Nema više vijesti na ovom izvoru")
            } else {
                Text(headlinesBySource.joinToString("\n"), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Povezane vijesti iz iste kategorije", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            slicneVijesti.forEachIndexed { index, vijest ->
                Text(
                    text = vijest.title,
                    modifier = Modifier
                        .clickable { navController.navigate("details/${vijest.uuid}") }
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
        ) {
            Button(
                onClick = { navController.popBackStack("home", inclusive = false) },
                modifier = Modifier.testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}