package com.exa.android.reflekt.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.profile.presentation.ProfileSkill
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProfileSkillsSection(skills: List<ProfileSkill>) {
    val dimens = DonutTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spacing20),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "EDIT",
                fontSize = DonutTextSize.small,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.spacing12),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing8),
            verticalArrangement = Arrangement.spacedBy(dimens.spacing8),
        ) {
            skills.forEach { skill -> SkillChip(skill) }
        }
    }
}

@Composable
private fun SkillChip(skill: ProfileSkill) {
    val dimens = DonutTheme.dimens
    val shape = RoundedCornerShape(DonutRadius.md)
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (skill.isHighlighted) primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface,
            )
            .border(
                dimens.borderThin,
                if (skill.isHighlighted) primary.copy(alpha = 0.3f)
                else DonutTheme.colorTokens.cardBorder,
                shape,
            )
            .padding(horizontal = dimens.spacing12, vertical = dimens.spacing6),
    ) {
        Text(
            text = skill.name,
            fontSize = DonutTextSize.body,
            fontWeight = if (skill.isHighlighted) FontWeight.Medium else FontWeight.Normal,
            color = if (skill.isHighlighted) primary
                   else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
