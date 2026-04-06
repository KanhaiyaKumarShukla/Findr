package com.exa.android.reflekt.feature.home.presentation.event_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun EventDetailScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
) {
    val event = sampleEventDetail()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
        ) {
            // Hero Section
            HeroSection(
                categoryBadge = event.categoryBadge,
                typeBadge = event.typeBadge,
                onBack = onBack,
            )

            // Content
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Title & Date
                TitleSection(
                    title = event.title,
                    dateTime = event.dateTime,
                )

                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing16))

                // Organizer
                OrganizerRow(
                    organizerName = event.organizerName,
                    avatarColorArgb = event.organizerAvatarColor,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                ActionButtonsRow()

                Spacer(modifier = Modifier.height(28.dp))

                // About
                AboutSection(description = event.description)

                Spacer(modifier = Modifier.height(28.dp))

                // Location
                LocationSection(
                    isVirtual = event.isVirtual,
                    locationName = event.locationName,
                    locationAddress = event.locationAddress,
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Participants
                ParticipantsSection(
                    registered = event.registeredCount,
                    maxParticipants = event.maxParticipants,
                    avatarColors = event.participantAvatarColors,
                )
            }
        }

        // Fixed Bottom Bar
        BottomRegisterBar(
            onRegister = onRegister,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroSection(
    categoryBadge: String,
    typeBadge: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
    ) {
        // Gradient background (placeholder for image)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DonutTheme.staticColorTokens.accentBlue.copy(alpha = 0.6f),
                            DonutTheme.staticColorTokens.accentIndigo.copy(alpha = 0.8f),
                        ),
                    ),
                ),
        )

        // Bottom gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                ),
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12)) {
                CircleIconButton(
                    icon = Icons.Default.FavoriteBorder,
                    onClick = { },
                )
                CircleIconButton(
                    icon = Icons.Default.Share,
                    onClick = { },
                )
            }
        }

        // Category badges
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
        ) {
            Badge(
                text = typeBadge,
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
            )
            Badge(
                text = categoryBadge,
                backgroundColor = Color.White.copy(alpha = 0.2f),
                textColor = Color.White,
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun Badge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        ),
        color = textColor,
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun TitleSection(
    title: String,
    dateTime: String,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing8))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = dateTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OrganizerRow(
    organizerName: String,
    avatarColorArgb: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DonutTheme.dimens.spacing12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(avatarColorArgb.toInt())),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column {
                Text(
                    text = "Hosted by",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = organizerName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Text(
            text = "Follow",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = { },
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

@Composable
private fun ActionButtonsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
    ) {
        ActionButton(
            icon = Icons.Default.EventAvailable,
            label = "Add to Cal",
            modifier = Modifier.weight(1f),
        )
        ActionButton(
            icon = Icons.Default.Share,
            label = "Share Event",
            modifier = Modifier.weight(1f),
        )
        ActionButton(
            icon = Icons.Default.Forum,
            label = "Chat",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { },
            )
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutSection(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "About Event",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 22.sp,
        )

        if (!expanded) {
            Text(
                text = "Read more",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = DonutTheme.dimens.spacing4)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = true },
                    ),
            )
        }
    }
}

@Composable
private fun LocationSection(
    isVirtual: Boolean,
    locationName: String,
    locationAddress: String,
) {
    Column {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                ),
        ) {
            if (!isVirtual) {
                // Map placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    // Pin indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, Color.White, CircleShape),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.padding(DonutTheme.dimens.spacing16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                        .background(
                            if (isVirtual) DonutTheme.staticColorTokens.accentBlue.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isVirtual) Icons.Default.Videocam else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isVirtual) DonutTheme.staticColorTokens.accentBlue else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = locationAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipantsSection(
    registered: Int,
    maxParticipants: Int,
    avatarColors: List<Long>,
) {
    val spotsLeft = maxParticipants - registered
    val progress = registered.toFloat() / maxParticipants.toFloat()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Participants",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                )
                .padding(DonutTheme.dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing16),
        ) {
            // Stacked avatars
            Box(modifier = Modifier.width((avatarColors.size * 24 + 8).dp)) {
                avatarColors.forEachIndexed { index, colorArgb ->
                    Box(
                        modifier = Modifier
                            .offset(x = (index * 24).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(Color(colorArgb.toInt())),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                // +N badge
                if (registered > avatarColors.size) {
                    Box(
                        modifier = Modifier
                            .offset(x = (avatarColors.size * 24).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+${registered - avatarColors.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Progress info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$registered registered",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (spotsLeft > 0) "$spotsLeft spots left" else "Event full",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun BottomRegisterBar(
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            )
            .padding(horizontal = 20.dp)
            .padding(top = DonutTheme.dimens.spacing40, bottom = DonutTheme.dimens.spacing16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing16),
        ) {
            Column {
                Text(
                    text = "PRICE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Button(
                onClick = onRegister,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                ),
            ) {
                Text(
                    text = "Register for Event",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sample data
// ═══════════════════════════════════════════════════════════════════════════════

private data class EventDetail(
    val title: String,
    val dateTime: String,
    val typeBadge: String,
    val categoryBadge: String,
    val organizerName: String,
    val organizerAvatarColor: Long,
    val description: String,
    val isVirtual: Boolean,
    val locationName: String,
    val locationAddress: String,
    val registeredCount: Int,
    val maxParticipants: Int,
    val participantAvatarColors: List<Long>,
)

private fun sampleEventDetail() = EventDetail(
    title = "Intro to UI/UX: Prototyping Workshop",
    dateTime = "Oct 24, 2:00 PM - 4:00 PM",
    typeBadge = "Physical",
    categoryBadge = "Design",
    organizerName = "Sarah Jenkins",
    organizerAvatarColor = 0xFF13B6EC,
    description = "Join us for an immersive deep dive into modern design principles. Whether you're a beginner looking to understand the basics of Figma or an experienced designer wanting to refine your prototyping skills, this workshop is for you. We will cover wireframing, high-fidelity mockups, and interaction design basics. Laptop required!",
    isVirtual = false,
    locationName = "Student Union, Room 304",
    locationAddress = "1200 University Ave, Seattle, WA",
    registeredCount = 45,
    maxParticipants = 50,
    participantAvatarColors = listOf(0xFF13B6EC, 0xFF6366F1, 0xFFA855F7, 0xFFEF4444),
)
