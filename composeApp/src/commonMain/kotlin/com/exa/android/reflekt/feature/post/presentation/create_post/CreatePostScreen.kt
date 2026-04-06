package com.exa.android.reflekt.feature.post.presentation.create_post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel,
    onCancel: () -> Unit,
    onPostSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isPostSuccess) {
        onPostSuccess()
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(onCancel = onCancel)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp, top = DonutTheme.dimens.spacing4),
                ) {
                    HeroSection()

                    Spacer(modifier = Modifier.height(24.dp))

                    PostContentSection(
                        title = uiState.title,
                        content = uiState.content,
                        onTitleChanged = { viewModel.onEvent(CreatePostEvent.TitleChanged(it)) },
                        onContentChanged = { viewModel.onEvent(CreatePostEvent.ContentChanged(it)) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    TagsSection(
                        tags = uiState.tags,
                        tagInput = uiState.currentTagInput,
                        onTagInputChanged = { viewModel.onEvent(CreatePostEvent.TagInputChanged(it)) },
                        onAddTag = { viewModel.onEvent(CreatePostEvent.AddTag) },
                        onRemoveTag = { viewModel.onEvent(CreatePostEvent.RemoveTag(it)) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    MediaSection(
                        mediaItems = uiState.mediaItems,
                        onAddMedia = { viewModel.onEvent(CreatePostEvent.AddMedia) },
                        onRemoveMedia = { viewModel.onEvent(CreatePostEvent.RemoveMedia(it)) },
                    )
                }
            }

            BottomActionBar(
                isPosting = uiState.isPosting,
                onShare = { viewModel.onEvent(CreatePostEvent.SharePost) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            uiState.errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(DonutTheme.dimens.spacing16)
                        .padding(bottom = 80.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    action = {
                        Text(
                            text = "Dismiss",
                            modifier = Modifier.clickable { viewModel.onEvent(CreatePostEvent.DismissError) },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                ) {
                    Text(text = error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (uiState.showMediaPickerDialog) {
            MediaPickerDialog(
                onMediaSelected = { name, size, type ->
                    viewModel.onEvent(CreatePostEvent.MediaSelected(name, size, type))
                },
                onDismiss = { viewModel.onEvent(CreatePostEvent.DismissMediaPicker) },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun Header(onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Cancel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onCancel,
                )
                .padding(horizontal = DonutTheme.dimens.spacing4, vertical = 2.dp),
        )

        Text(
            text = "Share a Post",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing40))
    }
}

@Composable
private fun HeroSection() {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.spacing40)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "Share your thoughts.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))

        Text(
            text = "Ask questions, share updates, or start a discussion.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PostContentSection(
    title: String,
    content: String,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
) {
    val colors = fieldColors()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
            Text(
                text = "Title",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                placeholder = {
                    Text(
                        text = "e.g. Looking for study group partners",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                singleLine = true,
                colors = colors,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
            Text(
                text = "Content",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = content,
                onValueChange = onContentChanged,
                placeholder = {
                    Text(
                        text = "What's on your mind? Share your thoughts, questions, or updates...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                colors = colors,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    tags: List<String>,
    tagInput: String,
    onTagInputChanged: (String) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
) {
    val colors = fieldColors()

    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
        Row {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "  (Optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }

        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
                verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
            ) {
                tags.forEach { tag ->
                    TagChip(label = tag, onRemove = { onRemoveTag(tag) })
                }
            }

            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))
        }

        OutlinedTextField(
            value = tagInput,
            onValueChange = onTagInputChanged,
            placeholder = {
                Text(
                    text = "Add tags (press enter)...",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    modifier = Modifier.size(DonutTheme.dimens.spacing16),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAddTag() }),
            colors = colors,
        )
    }
}

@Composable
private fun TagChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = CircleShape,
            )
            .padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove $label",
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .clickable(onClick = onRemove),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun BottomActionBar(
    isPosting: Boolean,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing12),
    ) {
        Button(
            onClick = onShare,
            enabled = !isPosting,
            modifier = Modifier
                .fillMaxWidth()
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
            if (isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Share Post",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(DonutTheme.dimens.spacing16),
                )
            }
        }
    }
}

@Composable
private fun MediaSection(
    mediaItems: List<AttachedMedia>,
    onAddMedia: () -> Unit,
    onRemoveMedia: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12)) {
        Row {
            Text(
                text = "Media",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "  (Optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }

        // Upload area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onAddMedia,
                )
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

                Text(
                    text = "Tap to add photos or videos",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))

                Text(
                    text = "JPG, PNG, MP4 up to 50MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }

        // Attached media list
        mediaItems.forEachIndexed { index, media ->
            MediaItemRow(media = media, onRemove = { onRemoveMedia(index) })
        }
    }
}

@Composable
private fun MediaItemRow(media: AttachedMedia, onRemove: () -> Unit) {
    val isVideo = media.type == MediaType.VIDEO
    val iconBgColor = if (isVideo) {
        DonutTheme.colorTokens.error.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }
    val iconTint = if (isVideo) DonutTheme.colorTokens.error else MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
            )
            .padding(DonutTheme.dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(DonutTheme.dimens.spacing40)
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                .background(iconBgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint,
            )
        }

        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = media.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = media.size,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove media",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MediaPickerDialog(
    onMediaSelected: (name: String, size: String, type: MediaType) -> Unit,
    onDismiss: () -> Unit,
) {
    data class SampleMedia(
        val name: String,
        val size: String,
        val type: MediaType,
    )

    val sampleMedia = listOf(
        SampleMedia("Campus_Photo.jpg", "2.3 MB", MediaType.IMAGE),
        SampleMedia("Team_Meeting.png", "1.8 MB", MediaType.IMAGE),
        SampleMedia("Project_Demo.mp4", "12.5 MB", MediaType.VIDEO),
        SampleMedia("Hackathon_Highlights.mp4", "8.7 MB", MediaType.VIDEO),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Select Media",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4)) {
                sampleMedia.forEach { media ->
                    val isVideo = media.type == MediaType.VIDEO
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
                            .clickable { onMediaSelected(media.name, media.size, media.type) }
                            .padding(DonutTheme.dimens.spacing12),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(DonutTheme.dimens.spacing40)
                                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                                .background(
                                    if (isVideo) DonutTheme.colorTokens.error.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isVideo) DonutTheme.colorTokens.error
                                else MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = media.size,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing8),
            )
        },
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
)
