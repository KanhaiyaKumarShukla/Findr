package com.exa.android.reflekt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.ui.theme.DonutLineHeight
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ProfileAboutSection(aboutText: String) {
    val dimens = DonutTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spacing20),
    ) {
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(dimens.spacing8))
        Text(
            text = aboutText,
            fontSize = DonutTextSize.body,
            lineHeight = DonutLineHeight.bodyLoose,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
