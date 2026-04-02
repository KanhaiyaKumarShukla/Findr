package com.exa.android.reflekt.feature.home.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FilterSelected -> {
                _uiState.update { state ->
                    state.copy(
                        selectedFilterIndex = event.index,
                        filterChips = state.filterChips.mapIndexed { i, chip ->
                            chip.copy(isSelected = i == event.index)
                        },
                    )
                }
            }

            is HomeEvent.LikeToggled -> {
                _uiState.update { state ->
                    state.copy(
                        feedPosts = state.feedPosts.map { post ->
                            if (post.id == event.postId) {
                                post.copy(
                                    isLiked = !post.isLiked,
                                    likeCount = if (post.isLiked) post.likeCount - 1
                                    else post.likeCount + 1,
                                )
                            } else post
                        },
                    )
                }
            }

            is HomeEvent.BottomNavClicked -> {
                _uiState.update { state ->
                    state.copy(
                        bottomNavItems = state.bottomNavItems.mapIndexed { i, item ->
                            item.copy(isSelected = i == event.index && !item.isFab)
                        },
                    )
                }
            }

            is HomeEvent.LiveEventClicked -> { /* TODO */ }
            is HomeEvent.SearchClicked -> { /* TODO */ }
            is HomeEvent.NotificationClicked -> { /* TODO */ }
            is HomeEvent.ProfileClicked -> { /* TODO */ }
            is HomeEvent.ViewAllLiveClicked -> { /* TODO */ }
            is HomeEvent.AnnouncementCtaClicked -> { /* TODO */ }
        }
    }

    private fun createInitialState(): HomeUiState {
        return HomeUiState(
            liveEvents = listOf(
                LiveEvent(
                    id = "live_1",
                    title = "Inter-University Hackathon",
                    subtitle = "Leaderboard: Team Alpha +40pts",
                    viewerCount = 1200,
                    gradientColors = listOf(Color(0xFF1E3A5F), Color(0xFF0EA0D1)),
                    icon = Icons.Default.Laptop,
                    isPulsing = true,
                ),
                LiveEvent(
                    id = "live_2",
                    title = "Student Council Elections",
                    subtitle = "75% Votes Counted • Results incoming",
                    viewerCount = 892,
                    gradientColors = listOf(Color(0xFF2D1B4E), Color(0xFF6366F1)),
                    icon = Icons.Default.HowToVote,
                ),
                LiveEvent(
                    id = "live_3",
                    title = "Football: Home vs. Rivals",
                    subtitle = "4th Quarter • Score 21-14",
                    viewerCount = 2341,
                    gradientColors = listOf(Color(0xFF1B4332), Color(0xFF22C55E)),
                    icon = Icons.Default.SportsSoccer,
                ),
            ),
            newsTickerItems = listOf(
                NewsTicker("news_1", "Free coffee at Library until 4 PM"),
                NewsTicker("news_2", "WinterFormal tickets on sale now!"),
                NewsTicker("news_3", "New menu at Dining Hall"),
                NewsTicker("news_4", "Guest lecture: AI in Ethics at 2PM"),
            ),
            filterChips = listOf(
                FilterChip("All Posts", isSelected = true),
                FilterChip("Events"),
                FilterChip("Clubs"),
                FilterChip("Academic"),
                FilterChip("Parties"),
            ),
            feedPosts = listOf(
                FeedPost(
                    id = "post_1",
                    authorName = "Sarah Jenkins",
                    authorSubtitle = "2 hours ago • Quad Area",
                    content = "Anyone else seeing this sunset from the library right now? Absolutely insane view for a study break. \uD83C\uDF05\u2728",
                    hasImage = true,
                    imageGradientColors = listOf(Color(0xFFE65100), Color(0xFF6A1B9A)),
                    imageIcon = Icons.Default.MenuBook,
                    likeCount = 245,
                    commentCount = 18,
                    avatarColor = Color(0xFF13B6EC),
                ),
            ),
            announcements = listOf(
                AnnouncementPost(
                    id = "announce_1",
                    source = "Campus Events Official",
                    sourceSubtitle = "4 hours ago",
                    title = "Volunteers Needed!",
                    description = "Looking for 10 more students to help with the International Food Fair next Friday. DM for details!",
                    ctaLabel = "Apply Now",
                    accentColor = Color(0xFF13B6EC),
                    icon = Icons.Default.Campaign,
                ),
            ),
            bottomNavItems = listOf(
                BottomNavItem("Home", Icons.Default.Home, isSelected = true),
                BottomNavItem("Discover", Icons.Default.Explore),
                BottomNavItem("Post", Icons.Default.Add, isFab = true),
                BottomNavItem("Groups", Icons.Default.Groups),
                BottomNavItem("Profile", Icons.Default.Person),
            ),
            notificationCount = 3,
        )
    }
}
