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
    val feedPosts: List<FeedPost> = emptyList(),
    val announcements: List<AnnouncementPost> = emptyList(),
    val bottomNavItems: List<BottomNavItem> = emptyList(),
    val selectedFilterIndex: Int = 0,
    val notificationCount: Int = 0,
)
