package com.exa.android.reflekt.feature.home.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class LiveEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val viewerCount: Int,
    val gradientColors: List<Color>,
    val icon: ImageVector,
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
    val imageGradientColors: List<Color> = emptyList(),
    val imageIcon: ImageVector? = null,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false,
    val avatarColor: Color,
)

data class ProjectPost(
    val id: String,
    val authorName: String,
    val authorSubtitle: String,
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val location: String? = null,
    val avatarColor: Color,
    val categoryLabel: String = "Project",
    val categoryColor: Color = Color(0xFF13B6EC),
)

data class BugPost(
    val id: String,
    val authorName: String,
    val authorSubtitle: String,
    val title: String,
    val errorSnippet: String,
    val collaboratorCount: Int = 0,
    val collaboratorColors: List<Color> = emptyList(),
    val avatarColor: Color,
    val categoryLabel: String = "Bug Help",
    val categoryColor: Color = Color(0xFFEF4444),
)

data class AnnouncementPost(
    val id: String,
    val source: String,
    val sourceSubtitle: String,
    val title: String,
    val description: String,
    val ctaLabel: String,
    val accentColor: Color,
    val icon: ImageVector,
)

sealed class FeedItem(val id: String) {
    data class Post(val data: FeedPost) : FeedItem(data.id)
    data class Project(val data: ProjectPost) : FeedItem(data.id)
    data class Bug(val data: BugPost) : FeedItem(data.id)
    data class Announcement(val data: AnnouncementPost) : FeedItem(data.id)
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
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
)
