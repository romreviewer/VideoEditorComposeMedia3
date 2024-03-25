package com.romreviewer.videoeditorcompose.presentation.model

import android.widget.Toast

data class ToastData(
    val message: String,
    val duration: Int = Toast.LENGTH_SHORT
)