package etf.ri.rma.newsfeedapp.extrastuff

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

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