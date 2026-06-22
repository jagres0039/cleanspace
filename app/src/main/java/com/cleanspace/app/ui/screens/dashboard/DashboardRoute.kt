package com.cleanspace.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.theme.CsPalette

@Composable
fun DashboardRoute(
    onRecommendationClick: (Recommendation) -> Unit,
    vm: DashboardViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    when (val s = state) {
        is ScanUiState.Loading -> DashboardCenter {
            CircularProgressIndicator(color = CsPalette.BrandGreen)
            Text(
                "Menganalisis penyimpanan…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        is ScanUiState.NeedsPermission -> DashboardCenter {
            Text(
                "Beri izin akses media dulu biar CleanSpace bisa menganalisis penyimpanan kamu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
        is ScanUiState.Error -> DashboardCenter {
            Text(
                s.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            CsButton(label = "Coba lagi", onClick = { vm.load() })
        }
        is ScanUiState.Ready -> DashboardScreen(
            state = s.data,
            onRecommendationClick = onRecommendationClick,
        )
    }
}

@Composable
private fun DashboardCenter(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}
