package com.exa.android.reflekt.feature.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val titleStart: String,
    val titleAccent: String,
    val subtitle: String,
)

private val pages = listOf(
    OnboardingPage(
        titleStart = "Collaborate ",
        titleAccent = "& Build",
        subtitle = "Connect with peers to work on projects, solve bugs, and organize campus events together.",
    ),
    OnboardingPage(
        titleStart = "Accelerate Your ",
        titleAccent = "Career",
        subtitle = "Stay updated with placement news, track company timelines, and get AI-powered eligibility insights.",
    ),
    OnboardingPage(
        titleStart = "Find Your ",
        titleAccent = "Community",
        subtitle = "Discover students nearby, filter by skills or year, and network within your campus.",
    ),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                OnboardingPageContent(pageIndex = pageIndex)
            }

            // Bottom section: dots + button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = DonutTheme.dimens.spacing40),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing32),
            ) {
                // Pagination dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .height(DonutTheme.dimens.spacing8)
                                .then(
                                    if (index == pagerState.currentPage) {
                                        Modifier.width(DonutTheme.dimens.spacing32)
                                    } else {
                                        Modifier.width(DonutTheme.dimens.spacing8)
                                    },
                                )
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage) {
                                        colors.primary
                                    } else {
                                        colors.paginationDotInactive
                                    },
                                ),
                        )
                    }
                }

                // Action button
                val isLastPage = pagerState.currentPage == pages.size - 1
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = DonutTheme.dimens.spacing8,
                        pressedElevation = DonutTheme.dimens.spacing4,
                    ),
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Next",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing8))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(pageIndex: Int) {
    val page = pages[pageIndex]
    val colors = DonutTheme.colorTokens

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Illustration area (top ~60%)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            contentAlignment = Alignment.Center,
        ) {
            when (pageIndex) {
                0 -> CollaborationIllustration()
                1 -> CareerIllustration()
                2 -> CommunityIllustration()
            }
        }

        // Text content area (bottom ~40%)
        Column(
            modifier = Modifier
                .weight(0.4f)
                .padding(horizontal = DonutTheme.dimens.spacing32)
                .padding(top = DonutTheme.dimens.spacing40),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.onBackground)) {
                        append(page.titleStart)
                    }
                    withStyle(SpanStyle(color = colors.primary)) {
                        append(page.titleAccent)
                    }
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp,
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing16))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )
        }
    }
}

// --- Page 1: Collaboration Illustration ---

@Composable
private fun CollaborationIllustration() {
    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .size(280.dp)
            .padding(DonutTheme.dimens.spacing16),
        contentAlignment = Alignment.Center,
    ) {
        // Central group icon
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.primary,
                            colors.accentBlue,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = colors.iconOnAccent,
                modifier = Modifier.size(48.dp),
            )
        }

        // Floating icons around
        FloatingIcon(
            icon = Icons.Default.Code,
            backgroundColor = colors.accentGreen,
            offsetX = (-75).dp,
            offsetY = (-55).dp,
            size = 38.dp,
        )
        FloatingIcon(
            icon = Icons.Default.Brush,
            backgroundColor = colors.accentPurple,
            offsetX = 75.dp,
            offsetY = (-45).dp,
            size = 34.dp,
        )
        FloatingIcon(
            icon = Icons.Default.Lightbulb,
            backgroundColor = colors.accentYellow,
            offsetX = (-70).dp,
            offsetY = 60.dp,
            size = 34.dp,
        )
        FloatingIcon(
            icon = Icons.Default.Handshake,
            backgroundColor = colors.accentOrange,
            offsetX = 70.dp,
            offsetY = 55.dp,
            size = 36.dp,
        )
    }
}

// --- Page 2: Career Illustration ---

@Composable
private fun CareerIllustration() {
    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .size(280.dp)
            .padding(DonutTheme.dimens.spacing32),
        contentAlignment = Alignment.Center,
    ) {
        // Central career icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.primary,
                            colors.accentIndigo,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = colors.iconOnAccent,
                modifier = Modifier.size(DonutTheme.dimens.spacing40),
            )
        }

        // Floating stat card left
        StatsCard(
            label = "Placement",
            offsetX = (-70).dp,
            offsetY = (-55).dp,
        )

        // Floating stat card right
        Box(
            modifier = Modifier
                .offset(x = 55.dp, y = 55.dp)
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
                .background(colors.cardBackground.copy(alpha = 0.9f))
                .border(1.dp, colors.cardBorder, RoundedCornerShape(DonutTheme.dimens.spacing12))
                .padding(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Eligible",
                    color = colors.cardText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Floating icons
        FloatingIcon(
            icon = Icons.Default.Work,
            backgroundColor = colors.accentOrange,
            offsetX = 65.dp,
            offsetY = (-50).dp,
            size = 34.dp,
        )
        FloatingIcon(
            icon = Icons.Default.Timeline,
            backgroundColor = colors.accentGreen,
            offsetX = (-65).dp,
            offsetY = 55.dp,
            size = DonutTheme.dimens.spacing32,
        )
    }
}

@Composable
private fun StatsCard(
    label: String,
    offsetX: Dp,
    offsetY: Dp,
) {
    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
            .background(colors.cardBackground.copy(alpha = 0.9f))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(DonutTheme.dimens.spacing12))
            .padding(10.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label.uppercase(),
                    color = colors.barChartLabel,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing8))
            // Mini bar chart
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.height(DonutTheme.dimens.spacing32),
            ) {
                val heights = listOf(0.4f, 0.6f, 0.5f, 0.85f)
                val barColors = listOf(
                    colors.barChartLow,
                    colors.barChartMid,
                    colors.barChartHigh,
                    colors.primary,
                )
                heights.forEachIndexed { index, fraction ->
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .fillMaxSize()
                            .weight(fraction)
                            .clip(RoundedCornerShape(2.dp))
                            .background(barColors[index]),
                    )
                }
            }
        }
    }
}

// --- Page 3: Community / Radar Illustration ---

@Composable
private fun CommunityIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pingScale",
    )
    val pingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pingAlpha",
    )

    val bounce1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (-8f),
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = ""
    )
    val bounce2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (-6f),
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = ""
    )
    val bounce3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (-10f),
        animationSpec = infiniteRepeatable(tween(1750, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = ""
    )

    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Map Base Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(colors.illustrationBackground.copy(alpha = 0.5f))
                .border(1.dp, colors.cardBorder.copy(alpha = 0.5f), CircleShape)
        ) {
            // Grid Lines
            val primaryColor = DonutTheme.staticColorTokens.accentBlue
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                val dotSpacing = 24.dp.toPx()
                val dotRadius = 1.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    var x = 0f
                    while (x < size.width) {
                        drawCircle(color = primaryColor, radius = dotRadius, center = Offset(x, y))
                        x += dotSpacing
                    }
                    y += dotSpacing
                }
            }
        }

        // Inner pulse circle (60%)
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.05f))
                .border(1.dp, colors.primary.copy(alpha = 0.3f), CircleShape)
        )

        // Outer dashed circle (85%)
        val dashEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        val strokeColor = colors.cardBorder.copy(alpha = 0.3f)
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
            drawCircle(
                color = strokeColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx(), pathEffect = dashEffect),
                radius = size.minDimension / 2f
            )
        }

        // Center "You" pin
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pingScale)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = pingAlpha))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(colors.primary)
                    .border(DonutTheme.dimens.spacing4, colors.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Floating avatar circles
        AvatarBubble(
            offsetX = 80.dp,
            offsetY = (-60).dp,
            size = DonutTheme.dimens.spacing40,
            bounceOffset = bounce1.dp,
            badgeColor = DonutTheme.staticColorTokens.accentGreen, // Green
            badgeIcon = Icons.Default.Code
        )
        AvatarBubble(
            offsetX = (-65).dp,
            offsetY = 65.dp,
            size = 36.dp,
            bounceOffset = bounce2.dp,
            badgeColor = DonutTheme.staticColorTokens.accentPurple, // Purple
            badgeIcon = Icons.Default.Brush
        )
        AvatarBubble(
            offsetX = (-75).dp,
            offsetY = (-45).dp,
            size = 28.dp,
            bounceOffset = 0.dp, // No bounce
            badgeColor = null,
            badgeIcon = null,
            alpha = 0.7f
        )
        AvatarBubble(
            offsetX = 65.dp,
            offsetY = 60.dp,
            size = DonutTheme.dimens.spacing32,
            bounceOffset = bounce3.dp,
            badgeColor = DonutTheme.staticColorTokens.accentYellow, // Yellow
            badgeIcon = Icons.Default.School
        )
    }
}

@Composable
private fun AvatarBubble(
    offsetX: Dp,
    offsetY: Dp,
    size: Dp,
    bounceOffset: Dp,
    badgeColor: Color?,
    badgeIcon: ImageVector?,
    alpha: Float = 1f,
) {
    val colors = DonutTheme.colorTokens

    Box(modifier = Modifier.offset(x = offsetX, y = offsetY + bounceOffset)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.cardBackground)
                .border(2.dp, colors.background, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = colors.iconOnAccent.copy(alpha = alpha),
                modifier = Modifier.size(size * 0.6f),
            )
        }
        if (badgeColor != null && badgeIcon != null) {
            Box(
                modifier = Modifier
                    .size(size * 0.45f)
                    .align(Alignment.BottomEnd)
                    .offset(x = (2).dp, y = (2).dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(1.5.dp, colors.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.25f)
                )
            }
        }
    }
}

@Composable
private fun FloatingIcon(
    icon: ImageVector,
    backgroundColor: Color,
    offsetX: Dp,
    offsetY: Dp,
    size: Dp,
) {
    val colors = DonutTheme.colorTokens

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.iconOnAccent,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
