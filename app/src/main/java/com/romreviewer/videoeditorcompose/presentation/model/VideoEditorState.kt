package com.romreviewer.videoeditorcompose.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class VideoEditorState(
    val videoUri: String? = null,
    val isPlaying: Boolean = false,
    val initTime: Long = 0L,
    val isExporting: Boolean = false,
    val isBackPressed: Boolean = false,
)