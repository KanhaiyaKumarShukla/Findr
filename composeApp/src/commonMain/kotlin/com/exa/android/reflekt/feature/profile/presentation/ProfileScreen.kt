package com.exa.android.reflekt.feature.profile.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileAboutSection
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileCoverSection
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileFeaturedProjects
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileIdentitySection
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileMetricsCard
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileSegmentedControl
import com.exa.android.reflekt.feature.profile.presentation.components.ProfileSkillsSection
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val dimens = DonutTheme.dimens

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ProfileEvent.EditProfileClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                Icon(Icons.Default.Edit, "Edit Profile", tint = Color.White)
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                ProfileCoverSection(
                    state = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = onNavigateBack,
                )
            }

            item { ProfileIdentitySection(state = uiState) }

            item { Spacer(modifier = Modifier.height(dimens.spacing16)) }

            item { ProfileMetricsCard(state = uiState) }

            item { Spacer(modifier = Modifier.height(dimens.spacing16)) }

            stickyHeader {
                ProfileSegmentedControl(
                    selectedTab = uiState.selectedTab,
                    onEvent = viewModel::onEvent,
                )
            }

            item { Spacer(modifier = Modifier.height(dimens.spacing8)) }

            item { ProfileSkillsSection(skills = uiState.skills) }

            item { Spacer(modifier = Modifier.height(dimens.spacing24)) }

            item {
                ProfileFeaturedProjects(
                    projects = uiState.featuredProjects,
                    onEvent = viewModel::onEvent,
                )
            }

            item { Spacer(modifier = Modifier.height(dimens.spacing24)) }

            item { ProfileAboutSection(aboutText = uiState.aboutText) }

            item { Spacer(modifier = Modifier.height(dimens.spacing80)) }
        }
    }
}
