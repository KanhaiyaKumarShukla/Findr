package com.exa.android.reflekt.feature.profile.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.TabSelected -> {
                val tab = ProfileTab.entries.getOrElse(event.index) { ProfileTab.PROFILE }
                _uiState.update { it.copy(selectedTab = tab) }
            }
            is ProfileEvent.EditProfileClicked -> { /* TODO */ }
            is ProfileEvent.SettingsClicked -> { /* TODO */ }
            is ProfileEvent.LinkedInClicked -> { /* TODO */ }
            is ProfileEvent.GitHubClicked -> { /* TODO */ }
            is ProfileEvent.ProjectClicked -> { /* TODO */ }
            is ProfileEvent.AddProjectClicked -> { /* TODO */ }
        }
    }

    private fun createInitialState(): ProfileUiState = ProfileUiState(
        name = "Sarah Jenkins",
        isVerified = true,
        department = "Computer Science & Engineering",
        classInfo = "MIT Class of '25",
        statusLabel = "On Campus",
        isOnline = true,
        connections = 542,
        projectCount = 12,
        rating = 4.8f,
        selectedTab = ProfileTab.PROFILE,
        skills = listOf(
            ProfileSkill("React Native", isHighlighted = true),
            ProfileSkill("UI Design", isHighlighted = true),
            ProfileSkill("Python"),
            ProfileSkill("Swift"),
            ProfileSkill("Public Speaking"),
            ProfileSkill("Figma"),
        ),
        featuredProjects = listOf(
            FeaturedProject(
                id = "proj_1",
                title = "Campus Connect",
                description = "A social platform for university students to collaborate on projects.",
                category = "Mobile App",
                memberCount = 4,
                accentColorArgb = 0xFF13B6EC,
            ),
            FeaturedProject(
                id = "proj_2",
                title = "Grade Predictor",
                description = "Machine learning model to predict semester grades based on study hours.",
                category = "AI/ML",
                memberCount = 1,
                isSolo = true,
                accentColorArgb = 0xFF6366F1,
            ),
        ),
        aboutText = "Passionate about building mobile applications that solve real-world problems. Currently looking for summer internships in Software Engineering. I love hackathons and open source contributions.",
        avatarInitial = "S",
        avatarColorArgb = 0xFF13B6EC,
    )
}
