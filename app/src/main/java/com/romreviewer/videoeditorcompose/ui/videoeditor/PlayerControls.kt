package com.romreviewer.videoeditorcompose.ui.videoeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.romreviewer.videoeditorcompose.R

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    currentPosition: ClosedFloatingPointRange<Float>,
    onSliderValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onPlayPauseClicked: () -> Unit,
    isPlaying: () -> Boolean,
    onSaveClicked: () -> Unit
) {
    val isVideoPlaying = remember(isPlaying()) { isPlaying() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onClick = {
                onSaveClicked()
            }
        ) {
            Text(text = "Save")
        }
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onPlayPauseClicked() }) {
                Icon(
                    painter = painterResource(
                        id = if (isVideoPlaying)
                            R.drawable.baseline_pause_circle_24
                        else
                            R.drawable.baseline_play_circle_24,
                    ),
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        RangeSlider(
            value = currentPosition,
            steps = 100,
            onValueChange = { range -> onSliderValueChanged(range) },
            valueRange = 0f..100f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun PlayerControlsPreview() {
    PlayerControls(
        onPlayPauseClicked = {},
        isPlaying = { true },
        currentPosition = 0f..100f,
        onSliderValueChanged = {},
        onSaveClicked = {}
    )
}