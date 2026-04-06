package com.exa.android.reflekt.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exa.android.reflekt.core.data.EventRepository
import com.exa.android.reflekt.core.data.toLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * HomeViewModel — no Compose imports allowed.
 * All visual mapping (Color, ImageVector) happens in the composable layer.
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val sampleLiveEvents = createInitialState().liveEvents

    init {
        EventRepository.postedEvents
            .onEach { posted ->
                val userEvents = posted.map { it.toLiveEvent() }
                _uiState.update { state ->
                    state.copy(liveEvents = userEvents + sampleLiveEvents)
                }
            }
            .launchIn(viewModelScope)
    }

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

            is HomeEvent.LiveEventClicked      -> { /* TODO: navigate to live event detail */ }
            is HomeEvent.SearchClicked         -> { /* TODO: open search */ }
            is HomeEvent.NotificationClicked   -> { /* handled by Screen via NavEvent */ }
            is HomeEvent.ProfileClicked        -> { /* TODO: navigate to profile */ }
            is HomeEvent.ViewAllLiveClicked    -> { /* TODO */ }
            is HomeEvent.AnnouncementCtaClicked -> { /* TODO */ }
            is HomeEvent.ProjectEnrollClicked  -> { /* TODO: navigate to project detail */ }
            is HomeEvent.BugCollaborateClicked -> { /* TODO */ }

            is HomeEvent.PollVoted -> {
                _uiState.update { state ->
                    state.copy(
                        feedItems = state.feedItems.map { item ->
                            if (item is FeedItem.Poll && item.data.id == event.pollId && item.data.votedIndex == null) {
                                val updatedOptions = item.data.options.mapIndexed { i, option ->
                                    if (i == event.optionIndex) option.copy(voteCount = option.voteCount + 1)
                                    else option
                                }
                                FeedItem.Poll(
                                    item.data.copy(
                                        options = updatedOptions,
                                        votedIndex = event.optionIndex,
                                        totalVotes = item.data.totalVotes + 1,
                                    ),
                                )
                            } else item
                        },
                    )
                }
            }
        }
    }

    private fun createInitialState(): HomeUiState = HomeUiState(
        liveEvents = listOf(
            LiveEvent(
                id = "live_1",
                title = "Inter-University Hackathon",
                subtitle = "Leaderboard: Team Alpha +40pts",
                participantCount = 1200,
                gradientType = LiveGradientType.BLUE_TECH,
                iconType = LiveEventIconType.LAPTOP,
                isPulsing = true,
            ),
            LiveEvent(
                id = "live_2",
                title = "Student Council Elections",
                subtitle = "75% Votes Counted • Results incoming",
                participantCount = 892,
                gradientType = LiveGradientType.PURPLE_VOTE,
                iconType = LiveEventIconType.VOTE,
            ),
            LiveEvent(
                id = "live_3",
                title = "Football: Home vs. Rivals",
                subtitle = "4th Quarter • Score 21-14",
                participantCount = 2341,
                gradientType = LiveGradientType.GREEN_SPORTS,
                iconType = LiveEventIconType.SOCCER,
            ),
            LiveEvent(
                id = "live_4",
                title = "AI Workshop: LLMs 101",
                subtitle = "Prof. Kim presenting • Q&A open",
                participantCount = 478,
                gradientType = LiveGradientType.BLUE_TECH,
                iconType = LiveEventIconType.LAPTOP,
                isPulsing = true,
            ),
            LiveEvent(
                id = "live_5",
                title = "Spring Fest Campaign",
                subtitle = "Voting closes in 2 hours",
                participantCount = 1567,
                gradientType = LiveGradientType.ORANGE_CAMPAIGN,
                iconType = LiveEventIconType.CAMPAIGN,
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
                    authorSubtitle = "2 hours ago • Quad Area",
                    content = "Anyone else seeing this sunset from the library right now? Absolutely insane view for a study break. 🌅✨",
                    hasImage = true,
                    imageIconType = FeedImageIconType.CODE,
                    likeCount = 245,
                    commentCount = 18,
                    avatarColorArgb = 0xFF13B6EC,
                ),
            ),
            FeedItem.Project(
                ProjectPost(
                    id = "proj_1",
                    authorName = "David Chen",
                    authorSubtitle = "CS Junior • 2h ago",
                    title = "AI-Powered Note Taker 📝",
                    description = "Building a React Native app that summarizes lectures using OpenAI's API. Need a backend dev familiar with Python/FastAPI.",
                    tags = listOf("Python", "React Native", "OpenAI"),
                    location = "Library, 3rd Floor",
                    avatarColorArgb = 0xFF13B6EC,
                ),
            ),
            FeedItem.Post(
                FeedPost(
                    id = "post_2",
                    authorName = "Alex Rodriguez",
                    authorSubtitle = "3 hours ago • CS Lab",
                    content = "Just deployed my first full-stack app for the capstone project! Shoutout to the study group 🚀💻",
                    hasImage = true,
                    imageIconType = FeedImageIconType.CODE,
                    likeCount = 189,
                    commentCount = 32,
                    avatarColorArgb = 0xFF6366F1,
                ),
            ),
            FeedItem.Bug(
                BugPost(
                    id = "bug_1",
                    authorName = "Elena Rodriguez",
                    authorSubtitle = "Eng Senior • 45m ago",
                    title = "Docker Container Crash 🐳",
                    errorSnippet = "Error: standard_init_linux.go:228: exec\nuser process caused: exec format error",
                    collaboratorCount = 2,
                    collaboratorColorArgbs = listOf(0xFF13B6EC, 0xFF6366F1),
                    avatarColorArgb = 0xFFEF4444,
                ),
            ),
            FeedItem.Poll(
                PollPost(
                    id = "poll_1",
                    authorName = "Campus Life",
                    authorSubtitle = "1 hour ago • Student Council",
                    question = "Best study spot on campus? 📚",
                    options = listOf(
                        PollOption("Library 3rd Floor", voteCount = 142),
                        PollOption("Student Center Café", voteCount = 98),
                        PollOption("Engineering Lounge", voteCount = 67),
                        PollOption("Outdoor Quad", voteCount = 43),
                    ),
                    totalVotes = 350,
                    avatarColorArgb = 0xFFA855F7,
                ),
            ),
            FeedItem.Post(
                FeedPost(
                    id = "post_3",
                    authorName = "Priya Sharma",
                    authorSubtitle = "5 hours ago • Student Center",
                    content = "The new bubble tea place on campus is actually amazing! Taro milk tea is a must-try. 🧋",
                    hasImage = false,
                    likeCount = 312,
                    commentCount = 45,
                    avatarColorArgb = 0xFFA855F7,
                ),
            ),
        ),
        bottomNavItems = listOf(
            BottomNavItem("Home",     "home",     isSelected = true),
            BottomNavItem("Discover", "explore"),
            BottomNavItem("Post",     "add",      isFab = true),
            BottomNavItem("Chat",     "chat"),
            BottomNavItem("Profile",  "person"),
        ),
        notificationCount = 3,
    )
}
