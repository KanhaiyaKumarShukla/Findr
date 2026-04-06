package com.exa.android.reflekt.navigation

import kotlinx.serialization.Serializable

@Serializable data object OnboardingGraph
@Serializable data object AuthGraph
@Serializable data object HomeGraph
@Serializable data object PostGraph
@Serializable data object ProfileGraph
@Serializable data object ChatGraph

@Serializable data object OnboardingRoute
@Serializable data object LoginRoute
@Serializable data object RegistrationRoute
@Serializable data object ForgotPasswordRoute

@Serializable data object HomeRoute
@Serializable data object NotificationsRoute

@Serializable data object CreatePostSelectionRoute
@Serializable data object CreateProjectRoute
@Serializable data object CreateEventRoute
@Serializable data object CreateBugReportRoute
@Serializable data object CreatePostRoute

@Serializable data class ProjectDetailRoute(val projectId: String)
@Serializable data class BugDetailRoute(val bugId: String)
@Serializable data class EventDetailRoute(val eventId: String)
@Serializable data class PollDetailRoute(val pollId: String)

@Serializable data object ProfileRoute
@Serializable data object ChatRoute
@Serializable data class ChatDetailRoute(val conversationId: String)
