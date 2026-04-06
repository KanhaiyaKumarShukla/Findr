package com.exa.android.reflekt.core.data

import com.exa.android.reflekt.feature.home.presentation.LiveEvent
import com.exa.android.reflekt.feature.home.presentation.LiveGradientType
import com.exa.android.reflekt.feature.home.presentation.LiveEventIconType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PostedEvent(
    val id: String,
    val topic: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String?,
    val isVirtual: Boolean,
)

object EventRepository {

    private val _postedEvents = MutableStateFlow<List<PostedEvent>>(emptyList())
    val postedEvents: StateFlow<List<PostedEvent>> = _postedEvents.asStateFlow()

    fun addEvent(event: PostedEvent) {
        _postedEvents.update { it + event }
    }
}

fun PostedEvent.toLiveEvent(): LiveEvent = LiveEvent(
    id = id,
    title = topic,
    subtitle = "$date $time",
    participantCount = 0,
    gradientType = if (isVirtual) LiveGradientType.PURPLE_VOTE else LiveGradientType.BLUE_TECH,
    iconType = if (isVirtual) LiveEventIconType.LAPTOP else LiveEventIconType.CAMPAIGN,
    isPulsing = true,
)
