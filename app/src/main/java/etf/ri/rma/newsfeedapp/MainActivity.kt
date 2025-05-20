package etf.ri.rma.newsfeedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.AppTheme
import etf.ri.rma.newsfeedapp.navigacija.AppNavigation
import etf.ri.rma.newsfeedapp.ui.theme.NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeViewModel = AppTheme()

        setContent {
            val isDarkTheme = themeViewModel.isDarkMode

            NewsAppTheme(isDarkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CustomTopAppBar(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { themeViewModel.toggleTheme() }
                            )
                        }

                    ) {
                            innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilterChipComponent(
    dodijeljenaKategorija: String,
    odabranaKategorija: String,
    onClick: (String) -> Unit,
    tag: String
) {
    val odabrano = odabranaKategorija == dodijeljenaKategorija
    val gotovo: @Composable (() -> Unit)? = if (odabrano) {
        {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }
    } else null

    FilterChip(
        selected = odabrano,
        onClick = { onClick(dodijeljenaKategorija) },
        label = { Text(text = dodijeljenaKategorija) },
        modifier = Modifier.testTag(tag),
        leadingIcon = gotovo
    )
}

@Composable
fun CustomTopAppBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp) // Ovo je manja visina, prilagodi po želji
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NewsFeedApp",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}