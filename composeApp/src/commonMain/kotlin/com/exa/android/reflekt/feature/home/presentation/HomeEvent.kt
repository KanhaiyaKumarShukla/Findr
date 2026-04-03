package com.exa.android.reflekt.feature.home.presentation

sealed class HomeEvent {
    data class FilterSelected(val index: Int) : HomeEvent()
    data class LikeToggled(val postId: String) : HomeEvent()
    data class LiveEventClicked(val eventId: String) : HomeEvent()
    data object SearchClicked : HomeEvent()
    data object NotificationClicked : HomeEvent()
    data object ProfileClicked : HomeEvent()
    data object ViewAllLiveClicked : HomeEvent()
    data class BottomNavClicked(val index: Int) : HomeEvent()
    data class AnnouncementCtaClicked(val announcementId: String) : HomeEvent()
    data class ProjectEnrollClicked(val projectId: String) : HomeEvent()
    data class BugCollaborateClicked(val bugId: String) : HomeEvent()
    data class PollVoted(val pollId: String, val optionIndex: Int) : HomeEvent()
}
