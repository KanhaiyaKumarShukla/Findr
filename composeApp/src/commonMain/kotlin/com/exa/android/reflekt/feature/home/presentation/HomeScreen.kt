package com.exa.android.reflekt.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.home.presentation.components.AnnouncementCard
import com.exa.android.reflekt.feature.home.presentation.components.BottomNavBar
import com.exa.android.reflekt.feature.home.presentation.components.BugPostCard
import com.exa.android.reflekt.feature.home.presentation.components.FeedPostCard
import com.exa.android.reflekt.feature.home.presentation.components.FilterChipsRow
import com.exa.android.reflekt.feature.home.presentation.components.HomeTopNavBar
import com.exa.android.reflekt.feature.home.presentation.components.LiveNowSection
import com.exa.android.reflekt.feature.home.presentation.components.PollCard
import com.exa.android.reflekt.feature.home.presentation.components.ProjectPostCard
import com.exa.android.reflekt.feature.home.presentation.components.TrendingNewsTicker

/**
 * Home screen — thin orchestrator.
 *
 * All visual components live in [components/]. This composable is responsible only for:
 * - Collecting [HomeUiState]
 * - Wiring navigation callbacks to [HomeViewModel] events
 * - Assembling the [Scaffold] + [LazyColumn] layout
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProjectDetail: (projectId: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopNavBar(
                notificationCount = uiState.notificationCount,
                onEvent = { event ->
                    if (event is HomeEvent.NotificationClicked) {
                        onNavigateToNotifications()
                    } else {
                        viewModel.onEvent(event)
                    }
                },
            )
        },
        bottomBar = {
            BottomNavBar(
                items = uiState.bottomNavItems,
                onEvent = { event ->
                    if (event is HomeEvent.BottomNavClicked &&
                        uiState.bottomNavItems.getOrNull(event.index)?.isFab == true
                    ) {
                        onNavigateToCreatePost()
                    } else {
                        viewModel.onEvent(event)
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                LiveNowSection(
                    events = uiState.liveEvents,
                    onEvent = viewModel::onEvent,
                )
            }

            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    TrendingNewsTicker(items = uiState.newsTickerItems)
                    FilterChipsRow(chips = uiState.filterChips, onEvent = viewModel::onEvent)
                }
            }

            items(uiState.feedItems, key = { it.id }) { item ->
                when (item) {
                    is FeedItem.Post         -> FeedPostCard(item.data, viewModel::onEvent)
                    is FeedItem.Project      -> ProjectPostCard(
                        project = item.data,
                        onEvent = viewModel::onEvent,
                        onCardClick = { onNavigateToProjectDetail(item.data.id) },
                    )
                    is FeedItem.Bug          -> BugPostCard(item.data, viewModel::onEvent)
                    is FeedItem.Announcement -> AnnouncementCard(item.data, viewModel::onEvent)
                    is FeedItem.Poll         -> PollCard(item.data, viewModel::onEvent)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
