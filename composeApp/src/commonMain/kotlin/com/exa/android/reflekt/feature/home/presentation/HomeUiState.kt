package com.exa.android.reflekt.feature.home.presentation

// NOTE: No Compose (Color, ImageVector) imports here — domain state must be pure Kotlin.
// Compose types are mapped in the individual card composables.

// ─── Enums for visual variants ────────────────────────────────────────────────

enum class LiveGradientType {
    BLUE_TECH, PURPLE_VOTE, GREEN_SPORTS, ORANGE_CAMPAIGN
}

enum class LiveEventIconType {
    LAPTOP, VOTE, SOCCER, CAMPAIGN
}

enum class FeedImageIconType {
    CODE, AWARD, BUG_REPORT, ANNOUNCEMENT, NONE
}

// ─── Data models ─────────────────────────────────────────────────────────────

data class LiveEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val viewerCount: Int,
    val gradientType: LiveGradientType,
    val iconType: LiveEventIconType,
    val isPulsing: Boolean = false,
)

data class NewsTicker(
    val id: String,
    val text: String,
)

data class FilterChip(
    val label: String,
    val isSelected: Boolean = false,
)

data class FeedPost(
    val id: String,
    val authorName: String,
    val authorSubtitle: String,
    val location: String? = null,
    val content: String,
    val hasImage: Boolean = false,
    val imageIconType: FeedImageIconType = FeedImageIconType.NONE,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false,
    // Avatar colour as ARGB Long — no Compose import needed
    val avatarColorArgb: Long,
)

data class ProjectPost(
    val id: String,
    val authorName: String,
    val authorSubtitle: String,
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val location: String? = null,
    val avatarColorArgb: Long,
    val categoryLabel: String = "Project",
    // Colour key — mapped to actual Color in composable
    val categoryColorKey: String = "blue",
)

data class BugPost(
    val id: String,
    val authorName: String,
    val authorSubtitle: String,
    val title: String,
    val errorSnippet: String,
    val collaboratorCount: Int = 0,
    val collaboratorColorArgbs: List<Long> = emptyList(),
    val avatarColorArgb: Long,
    val categoryLabel: String = "Bug Help",
)

data class AnnouncementPost(
    val id: String,
    val source: String,
    val sourceSubtitle: String,
    val title: String,
    val description: String,
    val ctaLabel: String,
    val accentColorKey: String = "blue",
    val iconType: FeedImageIconType = FeedImageIconType.ANNOUNCEMENT,
)

sealed class FeedItem(val id: String) {
    data class Post(val data: FeedPost)             : FeedItem(data.id)
    data class Project(val data: ProjectPost)       : FeedItem(data.id)
    data class Bug(val data: BugPost)               : FeedItem(data.id)
    data class Announcement(val data: AnnouncementPost) : FeedItem(data.id)
}

data class BottomNavItem(
    val label: String,
    val iconKey: String,
    val isSelected: Boolean = false,
    val isFab: Boolean = false,
)

data class HomeUiState(
    val liveEvents: List<LiveEvent> = emptyList(),
    val newsTickerItems: List<NewsTicker> = emptyList(),
    val filterChips: List<FilterChip> = emptyList(),
    val feedItems: List<FeedItem> = emptyList(),
    val bottomNavItems: List<BottomNavItem> = emptyList(),
    val selectedFilterIndex: Int = 0,
    val notificationCount: Int = 0,
    val errorMessage: String? = null,
)
