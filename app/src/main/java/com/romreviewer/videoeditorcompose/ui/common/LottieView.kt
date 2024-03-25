/*
 * LottieView.kt
 * Module: COTO.common.layers.ui.components
 * Project: COTO
 * Copyright © 2022, Eve World Platform PTE LTD. All rights reserved.
 */

package com.romreviewer.videoeditorcompose.ui.common

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.romreviewer.videoeditorcompose.R

@Composable
fun LottieView(
    modifier: Modifier = Modifier,
    @RawRes anim: Int = R.raw.loading,
    iterations: Int = LottieConstants.IterateForever,
    contentScale: ContentScale = ContentScale.Fit,
    speed: Float = 1f,
    onAnimFinished: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(anim))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition,
        iterations = iterations,
        modifier = modifier,
        contentScale = contentScale,
        speed = speed
    )
    if (progress == 1.0f) {
        onAnimFinished()
    }
}
