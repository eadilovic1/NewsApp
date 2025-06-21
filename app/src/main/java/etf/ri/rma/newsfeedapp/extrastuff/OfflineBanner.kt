package etf.ri.rma.newsfeedapp.extrastuff

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun isConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun OfflineBanner(context: Context) {
    val show = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        show.value = !isConnected(context)
    }

    if (show.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nema internet konekcije — offline režim",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
