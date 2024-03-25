package com.romreviewer.videoeditorcompose.ui.videoeditor

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.romreviewer.videoeditorcompose.ui.common.LoadingDialog

@OptIn(UnstableApi::class)
@Composable
fun VideoEditorScreen(
    navController: NavController,
    viewModel: VideoEditorViewModel
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableStateOf(0f..100f) }
    var videoPermissionState by remember {
        mutableStateOf(Environment.isExternalStorageManager())
    }
    val state by viewModel.state.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    val storageActivityResultLauncher: ActivityResultLauncher<Intent> =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //Android is 11 (R) or above
            if (Environment.isExternalStorageManager()) {
                Log.i("MainActivity", "Storage Permissions Granted")
                videoPermissionState = true
                //Manage External Storage Permissions Granted
            } else {
                Toast.makeText(
                    context,
                    "Storage Permissions Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val uri = Uri.decode(viewModel.uri.orEmpty())
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
                setMediaSource(source)
                addAnalyticsListener(viewModel.analyticsExoPlayerListener())
                playWhenReady = false
                prepare()
            }
    }
    LaunchedEffect(state.isBackPressed) {
        if (state.isBackPressed) {
            navController.popBackStack()
        }
    }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            }
        )
        PlayerControls(
            onPlayPauseClicked = {
                isPlaying = !isPlaying
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            },
            isPlaying = {
                isPlaying
            },
            currentPosition = sliderPosition,
            onSliderValueChanged = {
                if (it.start != sliderPosition.start)
                    exoPlayer.seekTo((it.start * 1000).toLong())
                sliderPosition = it
            },
            onSaveClicked = {
                viewModel.trimVideo(
                    (sliderPosition.start * 1000).toLong(),
                    (sliderPosition.endInclusive * 1000).toLong()
                )
            }
        )
        if (videoPermissionState.not()) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            runCatching {
                                val intent = Intent()
                                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                val uri = Uri.fromParts("package", context.packageName, null)
                                intent.setData(uri)
                                storageActivityResultLauncher.launch(intent)
                            }
                        }
                    ) {
                        Text(text = "Grant Permission")
                    }
                },
                title = {
                    Text(text = "Permission Required")
                },
                text = {
                    Text(text = "Write permission is required to to save edited video.")
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        if (state.isExporting) {
            LoadingDialog(title = "Exporting Video")
        }

    }
}
