package com.romreviewer.videoeditorcompose.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog(
    title: String,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Column(modifier = modifier) {
            Text(text = title)
            LottieView(modifier = Modifier.fillMaxWidth())
        }
    }
}