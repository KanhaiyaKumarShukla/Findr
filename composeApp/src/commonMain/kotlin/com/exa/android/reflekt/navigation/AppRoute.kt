package com.exa.android.reflekt.navigation

import kotlinx.serialization.Serializable

@Serializable data object OnboardingGraph
@Serializable data object AuthGraph
@Serializable data object HomeGraph
@Serializable data object PostGraph
@Serializable data object ProfileGraph

@Serializable data object OnboardingRoute
@Serializable data object LoginRoute
@Serializable data object RegistrationRoute
@Serializable data object ForgotPasswordRoute

@Serializable data object HomeRoute
@Serializable data object NotificationsRoute

@Serializable data object CreatePostSelectionRoute
@Serializable data object CreateProjectRoute
@Serializable data object CreateEventRoute

@Serializable data class ProjectDetailRoute(val projectId: String)

@Serializable data object ProfileRoute
