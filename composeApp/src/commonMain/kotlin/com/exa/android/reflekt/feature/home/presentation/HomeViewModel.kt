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
                        feedItems = state.feedItems.map { item ->
                            if (item is FeedItem.Post && item.data.id == event.postId) {
                                FeedItem.Post(
                                    item.data.copy(
                                        isLiked = !item.data.isLiked,
                                        likeCount = if (item.data.isLiked) item.data.likeCount - 1
                                        else item.data.likeCount + 1,
                                    ),
                                )
                            } else item
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
            is HomeEvent.ProjectEnrollClicked -> { /* TODO */ }
            is HomeEvent.BugCollaborateClicked -> { /* TODO */ }
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
            feedItems = listOf(
                FeedItem.Post(
                    FeedPost(
                        id = "post_1",
                        authorName = "Sarah Jenkins",
                        authorSubtitle = "2 hours ago \u2022 Quad Area",
                        content = "Anyone else seeing this sunset from the library right now? Absolutely insane view for a study break. \uD83C\uDF05\u2728",
                        hasImage = true,
                        imageGradientColors = listOf(Color(0xFFE65100), Color(0xFF6A1B9A)),
                        imageIcon = Icons.Default.MenuBook,
                        likeCount = 245,
                        commentCount = 18,
                        avatarColor = Color(0xFF13B6EC),
                    ),
                ),
                FeedItem.Project(
                    ProjectPost(
                        id = "proj_1",
                        authorName = "David Chen",
                        authorSubtitle = "CS Junior \u2022 2h ago",
                        title = "AI-Powered Note Taker \uD83D\uDCDD",
                        description = "Building a React Native app that summarizes lectures using OpenAI's API. Need a backend dev familiar with Python/FastAPI.",
                        tags = listOf("Python", "React Native", "OpenAI"),
                        location = "Library, 3rd Floor",
                        avatarColor = Color(0xFF13B6EC),
                    ),
                ),
                FeedItem.Post(
                    FeedPost(
                        id = "post_2",
                        authorName = "Alex Rodriguez",
                        authorSubtitle = "3 hours ago \u2022 CS Lab",
                        content = "Just deployed my first full-stack app for the capstone project! Shoutout to the study group for keeping me sane during the all-nighter \uD83D\uDE80\uD83D\uDCBB",
                        hasImage = true,
                        imageGradientColors = listOf(Color(0xFF1A237E), Color(0xFF0EA0D1)),
                        imageIcon = Icons.Default.Laptop,
                        likeCount = 189,
                        commentCount = 32,
                        avatarColor = Color(0xFF6366F1),
                    ),
                ),
                FeedItem.Bug(
                    BugPost(
                        id = "bug_1",
                        authorName = "Elena Rodriguez",
                        authorSubtitle = "Eng Senior \u2022 45m ago",
                        title = "Docker Container Crash \uD83D\uDC33",
                        errorSnippet = "Error: standard_init_linux.go:228: exec\nuser process caused: exec format error",
                        collaboratorCount = 2,
                        collaboratorColors = listOf(Color(0xFF13B6EC), Color(0xFF6366F1)),
                        avatarColor = Color(0xFFEF4444),
                    ),
                ),
                FeedItem.Post(
                    FeedPost(
                        id = "post_3",
                        authorName = "Priya Sharma",
                        authorSubtitle = "5 hours ago \u2022 Student Center",
                        content = "The new bubble tea place on campus is actually amazing! Taro milk tea is a must-try. Line was long but totally worth it \uD83E\uDD64",
                        hasImage = false,
                        likeCount = 312,
                        commentCount = 45,
                        avatarColor = Color(0xFFA855F7),
                    ),
                ),
                FeedItem.Project(
                    ProjectPost(
                        id = "proj_2",
                        authorName = "Maya Patel",
                        authorSubtitle = "Design Senior \u2022 4h ago",
                        title = "Campus Marketplace App \uD83D\uDED2",
                        description = "Designing a buy/sell/trade platform for students. Looking for a Flutter dev and someone who knows Firebase.",
                        tags = listOf("Flutter", "Firebase", "UI/UX"),
                        location = "Design Lab, Room 204",
                        avatarColor = Color(0xFFA855F7),
                        categoryColor = Color(0xFFA855F7),
                    ),
                ),
                FeedItem.Post(
                    FeedPost(
                        id = "post_4",
                        authorName = "Marcus Thompson",
                        authorSubtitle = "6 hours ago \u2022 Gym",
                        content = "Intramural basketball signups close tonight! We need 2 more players for our team. DM me if you're in \uD83C\uDFC0",
                        hasImage = true,
                        imageGradientColors = listOf(Color(0xFFBF360C), Color(0xFFF57C00)),
                        imageIcon = Icons.Default.SportsSoccer,
                        likeCount = 87,
                        commentCount = 14,
                        avatarColor = Color(0xFFF97316),
                    ),
                ),
                FeedItem.Bug(
                    BugPost(
                        id = "bug_2",
                        authorName = "Liam Foster",
                        authorSubtitle = "CS Junior \u2022 3h ago",
                        title = "React State Not Updating \u26A1",
                        errorSnippet = "TypeError: Cannot read properties of\nundefined (reading 'map')\n  at UserList (UserList.jsx:12:18)",
                        collaboratorCount = 5,
                        collaboratorColors = listOf(Color(0xFF22C55E), Color(0xFFA855F7), Color(0xFFF97316)),
                        avatarColor = Color(0xFFF97316),
                    ),
                ),
                FeedItem.Post(
                    FeedPost(
                        id = "post_5",
                        authorName = "Emily Chen",
                        authorSubtitle = "8 hours ago \u2022 Library",
                        content = "Found a hidden study spot on the 4th floor of the library behind the archives. Super quiet and has outlets everywhere. You're welcome \uD83D\uDE09\uD83D\uDCDA",
                        hasImage = true,
                        imageGradientColors = listOf(Color(0xFF004D40), Color(0xFF26A69A)),
                        imageIcon = Icons.Default.MenuBook,
                        likeCount = 421,
                        commentCount = 56,
                        avatarColor = Color(0xFF22C55E),
                    ),
                ),
                FeedItem.Project(
                    ProjectPost(
                        id = "proj_3",
                        authorName = "Ryan Kim",
                        authorSubtitle = "CS Sophomore \u2022 9h ago",
                        title = "Smart Study Planner \uD83D\uDCCA",
                        description = "An ML-based app that creates personalized study schedules from your syllabus. Need someone experienced with NLP and Kotlin.",
                        tags = listOf("Kotlin", "ML", "NLP"),
                        location = "CS Building, Lab 3",
                        avatarColor = Color(0xFF22C55E),
                        categoryColor = Color(0xFF22C55E),
                    ),
                ),
                FeedItem.Post(
                    FeedPost(
                        id = "post_6",
                        authorName = "Jordan Williams",
                        authorSubtitle = "10 hours ago \u2022 Music Hall",
                        content = "Our a cappella group just nailed the rehearsal for this weekend's concert! Come support us Saturday at 7 PM in the auditorium \uD83C\uDFB6\uD83C\uDFA4",
                        hasImage = false,
                        likeCount = 156,
                        commentCount = 23,
                        avatarColor = Color(0xFFEAB308),
                    ),
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
