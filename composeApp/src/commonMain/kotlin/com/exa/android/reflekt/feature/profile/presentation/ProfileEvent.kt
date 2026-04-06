package com.exa.android.reflekt.feature.profile.presentation

sealed class ProfileEvent {
    data class TabSelected(val index: Int) : ProfileEvent()
    data object EditProfileClicked : ProfileEvent()
    data object SettingsClicked : ProfileEvent()
    data object LinkedInClicked : ProfileEvent()
    data object GitHubClicked : ProfileEvent()
    data class ProjectClicked(val projectId: String) : ProfileEvent()
    data object AddProjectClicked : ProfileEvent()
}
