package com.cleanspace.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

/** Full-screen scan-in-progress view with a brand-tinted spinner. */
@Composable
fun ScanLoading(title: String, message: String, onBack: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = title, onBack = onBack)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = CsPalette.BrandGreen, strokeWidth = 3.dp)
                Spacer(Modifier.size(Dimens.space16))
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorsExt.textSoft)
            }
        }
    }
}

/** Full-screen message view (empty / error / needs-permission). */
@Composable
fun ScanMessage(
    title: String,
    message: String,
    icon: ImageVector = CsIcons.Info,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = title, onBack = onBack)
        Box(Modifier.fillMaxSize().padding(Dimens.space24), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorsExt.textFaint, modifier = Modifier.size(40.dp))
                Spacer(Modifier.size(Dimens.space12))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorsExt.textSoft,
                    textAlign = TextAlign.Center,
                )
                if (actionLabel != null) {
                    Spacer(Modifier.size(Dimens.space16))
                    CsButton(label = actionLabel, onClick = onAction, style = CsButtonStyle.Secondary)
                }
            }
        }
    }
}
