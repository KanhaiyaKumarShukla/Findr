package com.exa.android.reflekt.feature.profile.presentation

enum class ProfileTab { PROFILE, ACTIVITY, RESUME }

data class ProfileSkill(
    val name: String,
    val isHighlighted: Boolean = false,
)

data class FeaturedProject(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val memberCount: Int,
    val isSolo: Boolean = false,
    val accentColorArgb: Long = 0xFF13B6EC,
)

data class ProfileUiState(
    val name: String = "",
    val isVerified: Boolean = false,
    val department: String = "",
    val classInfo: String = "",
    val statusLabel: String = "",
    val isOnline: Boolean = false,
    val connections: Int = 0,
    val projectCount: Int = 0,
    val rating: Float = 0f,
    val selectedTab: ProfileTab = ProfileTab.PROFILE,
    val skills: List<ProfileSkill> = emptyList(),
    val featuredProjects: List<FeaturedProject> = emptyList(),
    val aboutText: String = "",
    val avatarInitial: String = "",
    val avatarColorArgb: Long = 0xFF13B6EC,
)
