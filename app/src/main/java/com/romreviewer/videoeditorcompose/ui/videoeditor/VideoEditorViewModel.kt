package com.romreviewer.videoeditorcompose.ui.videoeditor

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.romreviewer.videoeditorcompose.presentation.model.VideoEditorState
import com.romreviewer.videoeditorcompose.videoeditortools.CustomMuxer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


@OptIn(UnstableApi::class)
class VideoEditorViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var uri = savedStateHandle.get<String>("uri")
    val state = MutableStateFlow(VideoEditorState())
    var initTime = 0L
    var playTime = 0L
    var pauseTime = 0L
    var pressedPaused = 0
    var totalTime = 0L

    fun analyticsExoPlayerListener() = object : AnalyticsListener {
        override fun onIsPlayingChanged(
            eventTime: AnalyticsListener.EventTime,
            isPlaying: Boolean
        ) {
            if (isPlaying) {
                if (initTime != 0L) pauseTime += System.currentTimeMillis() - initTime
                initTime = System.currentTimeMillis()
            } else {
                if (initTime != 0L) playTime += System.currentTimeMillis() - initTime
                initTime = System.currentTimeMillis()
                pressedPaused++
            }
            totalTime = playTime + pauseTime
            Log.e("onIsPlaying", "PLAYTIME: $playTime")
            Log.e("onIsPlaying", "PRESSEDPAUSE: $pressedPaused")
            Log.e("onIsPlaying", "PAUSETIME: $pauseTime")
            Log.e("onIsPlaying", "TOTALTIME: $totalTime")
            /*if (exoPlayer.currentPosition >= (exoPlayer.duration - (sliderPosition.endInclusive * 1000))) {
                exoPlayer.seekTo((exoPlayer.duration - (sliderPosition.endInclusive * 1000)).toLong())
                exoPlayer.pause()
            }*/
            super.onIsPlayingChanged(eventTime, isPlaying)
        }
    }

    private val transformerListener: Transformer.Listener =
        object : Transformer.Listener {
            override fun onCompleted(composition: Composition, result: ExportResult) {
                Log.i("VideoEditorViewModel", "Completed $result")
                updateExportLoading(false)
            }

            override fun onError(
                composition: Composition, result: ExportResult,
                exception: ExportException
            ) {
                Log.e("VideoEditorViewModel", "Error $exception")
                updateExportLoading(false)
            }
        }

    fun trimVideo(startPosition: Long, endPosition: Long) {
        val transformer = Transformer.Builder(getApplication())
            .setVideoMimeType(MimeTypes.VIDEO_H265)
            .addListener(transformerListener)
            .setMuxerFactory(CustomMuxer.Factory())
            .build()
        val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(startPosition) // start at 10 seconds
            .setEndPositionMs(endPosition) // end at 20 seconds
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(uri?.toUri())
            .setClippingConfiguration(clippingConfiguration)
            .build()
        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .build()
        kotlin.runCatching {
            updateExportLoading(true)
            transformer
                .start(
                    editedMediaItem,
                    Environment.getExternalStorageDirectory().absolutePath + "/trimmed.mp4"
                )
        }.onFailure {
            Log.e("VideoEditorViewModel", "Error", it)
        }
    }

    private fun updateExportLoading(isLoading: Boolean) {
        state.update { it.copy(isExporting = isLoading) }
    }
}