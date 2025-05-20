package etf.ri.rma.newsfeedapp.navigacija

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(navController = navController)
        }
        composable("filters") {
            FilterScreen(navController = navController)
        }
        composable("details/{id}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("id")
            newsId?.let { NavigationState.selectNewsItem(it) }
            if (newsId != null) {
                NewsDetailsScreen(navController = navController, id = newsId)
            }
        }
    }
}