package etf.ri.rma.newsfeedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import etf.ri.rma.newsfeedapp.extrastuff.CustomTopAppBar
import etf.ri.rma.newsfeedapp.model.AppTheme
import etf.ri.rma.newsfeedapp.navigacija.AppNavigation
import etf.ri.rma.newsfeedapp.navigacija.NavigationState
import etf.ri.rma.newsfeedapp.ui.theme.NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicijalizacija baze podataka
        NavigationState.initializeDatabase(this)
        NavigationState.initvrijemePozivaPoKategoriji()

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
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        // Ovdje možete dodati dodatne operacije čišćenja ako je potrebno
        super.onDestroy()
    }
}