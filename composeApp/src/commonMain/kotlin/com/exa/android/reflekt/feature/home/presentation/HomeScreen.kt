package com.exa.android.reflekt.feature.home.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.appColors

private val LiveRed = Color(0xFFEF4444)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopNavBar(
                notificationCount = uiState.notificationCount,
                onEvent = { event ->
                    if (event is HomeEvent.NotificationClicked) {
                        onNavigateToNotifications()
                    } else {
                        viewModel.onEvent(event)
                    }
                },
            )
        },
        bottomBar = {
            BottomNavBar(
                items = uiState.bottomNavItems,
                onEvent = { event ->
                    if (event is HomeEvent.BottomNavClicked &&
                        uiState.bottomNavItems.getOrNull(event.index)?.isFab == true
                    ) {
                        onNavigateToCreatePost()
                    } else {
                        viewModel.onEvent(event)
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Live Now Section
            item {
                LiveNowSection(
                    events = uiState.liveEvents,
                    onEvent = viewModel::onEvent,
                )
            }

            // Sticky: Trending News Ticker + Filter Chips
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    TrendingNewsTicker(items = uiState.newsTickerItems)
                    FilterChipsRow(
                        chips = uiState.filterChips,
                        onEvent = viewModel::onEvent,
                    )
                }
            }

            // Mixed Feed Items
            items(uiState.feedItems, key = { it.id }) { item ->
                when (item) {
                    is FeedItem.Post -> FeedPostCard(
                        post = item.data,
                        onEvent = viewModel::onEvent,
                    )

                    is FeedItem.Project -> ProjectPostCard(
                        project = item.data,
                        onEvent = viewModel::onEvent,
                    )

                    is FeedItem.Bug -> BugPostCard(
                        bug = item.data,
                        onEvent = viewModel::onEvent,
                    )

                    is FeedItem.Announcement -> AnnouncementCard(
                        announcement = item.data,
                        onEvent = viewModel::onEvent,
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ─── Top Navigation Bar ─────────────────────────────────────────────────────

@Composable
private fun TopNavBar(
    notificationCount: Int,
    onEvent: (HomeEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Profile picture placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onEvent(HomeEvent.ProfileClicked) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title
        Column {
            Text(
                text = "College Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "CAMPUS LIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Search
        IconButton(onClick = { onEvent(HomeEvent.SearchClicked) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Notification bell with badge
        Box {
            IconButton(onClick = { onEvent(HomeEvent.NotificationClicked) }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LiveRed),
                )
            }
        }
    }
}

// ─── Live Now Section ────────────────────────────────────────────────────────

@Composable
private fun LiveNowSection(
    events: List<LiveEvent>,
    onEvent: (HomeEvent) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Column(modifier = Modifier.padding(top = 16.dp)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .graphicsLayer { alpha = pulseAlpha }
                    .background(LiveRed),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Live Now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onEvent(HomeEvent.ViewAllLiveClicked) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal card list
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(events, key = { it.id }) { event ->
                LiveEventCard(
                    event = event,
                    pulseAlpha = pulseAlpha,
                    onClick = { onEvent(HomeEvent.LiveEventClicked(event.id)) },
                )
            }
        }
    }
}

@Composable
private fun LiveEventCard(
    event: LiveEvent,
    pulseAlpha: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(176.dp)
            .then(
                if (event.isPulsing) {
                    Modifier.border(
                        width = 2.dp,
                        color = LiveRed.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(16.dp),
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                    )
                },
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
    ) {
        // Gradient background with icon watermark
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(event.gradientColors)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = event.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp),
            )
        }

        // Bottom gradient scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    ),
                ),
        )

        // LIVE badge
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(LiveRed, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Podcasts,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = "LIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        // Viewer count badge
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = formatViewerCount(event.viewerCount),
                fontSize = 10.sp,
                color = Color.White,
            )
        }

        // Title & subtitle overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
        ) {
            Text(
                text = event.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ─── Trending News Ticker ────────────────────────────────────────────────────

@Composable
private fun TrendingNewsTicker(items: List<NewsTicker>) {
    val scrollState = rememberScrollState()

    // Auto-scroll marquee
    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(
                scrollState.maxValue,
                animationSpec = tween(
                    durationMillis = 15000,
                    easing = LinearEasing,
                ),
            )
            scrollState.scrollTo(0)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Label
        Text(
            text = "CAMPUS BUZZ",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .padding(start = 16.dp, end = 12.dp),
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(16.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        )

        // Scrolling items
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "#",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = item.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// ─── Filter Chips Row ────────────────────────────────────────────────────────

@Composable
private fun FilterChipsRow(
    chips: List<FilterChip>,
    onEvent: (HomeEvent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 20.dp),
    ) {
        itemsIndexed(chips) { index, chip ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (chip.isSelected) {
                            Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surface)
                        },
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { onEvent(HomeEvent.FilterSelected(index)) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = chip.label,
                    fontSize = 14.sp,
                    fontWeight = if (chip.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (chip.isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Feed Post Card ──────────────────────────────────────────────────────────

@Composable
private fun FeedPostCard(
    post: FeedPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, appColors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(post.avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = post.avatarColor,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = post.authorSubtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Content text
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // Image placeholder
        if (post.hasImage && post.imageGradientColors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Brush.linearGradient(post.imageGradientColors)),
                contentAlignment = Alignment.Center,
            ) {
                post.imageIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(56.dp),
                    )
                }
            }
        }

        // Action bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Like
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEvent(HomeEvent.LikeToggled(post.id)) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) LiveRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "${post.likeCount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Comment
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "${post.commentCount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Share
            IconButton(
                onClick = { },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

// ─── Project Post Card ───────────────────────────────────────────────────────

@Composable
private fun ProjectPostCard(
    project: ProjectPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, appColors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        // Header: Avatar + Name + Tag + three-dot menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(project.avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = project.avatarColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = project.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(project.categoryColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = project.categoryLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = project.categoryColor,
                        )
                    }
                }
                Text(
                    text = project.authorSubtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title
        Text(
            text = project.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = project.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tech tags
        if (project.tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                project.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp),
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Footer: Location + Enroll button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (project.location != null) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = project.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(HomeEvent.ProjectEnrollClicked(project.id)) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Enroll",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

// ─── Bug Post Card ───────────────────────────────────────────────────────────

@Composable
private fun BugPostCard(
    bug: BugPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, bug.categoryColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        // Header: Avatar + Name + Tag + three-dot menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bug.avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = bug.avatarColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = bug.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(bug.categoryColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = bug.categoryLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = bug.categoryColor,
                        )
                    }
                }
                Text(
                    text = bug.authorSubtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title
        Text(
            text = bug.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Error code snippet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    1.dp,
                    bug.categoryColor.copy(alpha = 0.2f),
                    RoundedCornerShape(10.dp),
                )
                .padding(12.dp),
        ) {
            Text(
                text = bug.errorSnippet,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Footer: Overlapping avatars + Collaborate button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Overlapping collaborator circles
            if (bug.collaboratorColors.isNotEmpty()) {
                Box(modifier = Modifier.height(28.dp)) {
                    bug.collaboratorColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .offset(x = (index * 18).dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .background(color.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    // "+N" count
                    if (bug.collaboratorCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = (bug.collaboratorColors.size * 18).dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .background(appColors.cardBackground),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+${bug.collaboratorCount}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Collaborate button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp),
                    )
                    .clickable { onEvent(HomeEvent.BugCollaborateClicked(bug.id)) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Collaborate",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

// ─── Announcement Card ───────────────────────────────────────────────────────

@Composable
private fun AnnouncementCard(
    announcement: AnnouncementPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, appColors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
    ) {
        // Header
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(announcement.accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = announcement.icon,
                    contentDescription = null,
                    tint = announcement.accentColor,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = announcement.source,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = announcement.sourceSubtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(announcement.accentColor.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    announcement.accentColor.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp),
                )
                .padding(14.dp),
        ) {
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onEvent(HomeEvent.AnnouncementCtaClicked(announcement.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = announcement.accentColor.copy(alpha = 0.12f),
                    contentColor = announcement.accentColor,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text = announcement.ctaLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ─── Bottom Navigation Bar ───────────────────────────────────────────────────

@Composable
private fun BottomNavBar(
    items: List<BottomNavItem>,
    onEvent: (HomeEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        // Separator line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .align(Alignment.TopCenter),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom,
        ) {
            items.forEachIndexed { index, item ->
                if (item.isFab) {
                    // Center FAB
                    Box(
                        modifier = Modifier
                            .offset(y = (-16).dp)
                            .size(56.dp)
                            .shadow(12.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onEvent(HomeEvent.BottomNavClicked(index)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                } else {
                    // Regular nav item
                    Column(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onEvent(HomeEvent.BottomNavClicked(index)) }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (item.isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = if (item.isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ─── Utility ─────────────────────────────────────────────────────────────────

private fun formatViewerCount(count: Int): String {
    return when {
        count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k watching"
        else -> "$count watching"
    }
}
