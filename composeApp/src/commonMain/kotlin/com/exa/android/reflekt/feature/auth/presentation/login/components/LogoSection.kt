package com.exa.android.reflekt.feature.auth.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutLineHeight

@Composable
internal fun LogoSection() {
    val colors = DonutTheme.colorTokens

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = DonutTheme.dimens.spacing16),
    ) {
        Box(
            modifier = Modifier
                .size(DonutTheme.dimens.spacing80)
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing20))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.primary,
                            colors.accentBlue,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "CampusConnect Logo",
                tint = colors.iconOnAccent,
                modifier = Modifier.size(DonutTheme.dimens.spacing40),
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing24))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colors.onBackground)) {
                    append("Campus")
                }
                withStyle(SpanStyle(color = colors.primary)) {
                    append("Connect")
                }
            },
            fontSize = DonutTextSize.hero,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing8))

        Text(
            text = "Connect. Collaborate. Get Hired.",
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = DonutLineHeight.title,
        )
    }
}
