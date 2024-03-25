package com.romreviewer.videoeditorcompose.ui.home

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.romreviewer.videoeditorcompose.R


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val openGalleryLauncher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            navController.navigate("video_editor/abc")
        }
    }
    val photoPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                Log.i("HomeScreen", "uri: ${it.path}")
                navController.navigate("video_editor/${Uri.encode(it.toString())}")
            }
        }
    val videoPermissionState =
        rememberPermissionState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_VIDEO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (videoPermissionState.status.isGranted)
                        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context))
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        else
                            openGalleryLauncher.launch("video/*")
                    else
                        videoPermissionState.launchPermissionRequest()
                }
            ) {
                Text(stringResource(R.string.select_video))
            }
        }
        if (videoPermissionState.status.isGranted.not())
            AlertDialog(
                title = {
                    Text(text = "Permission Required")
                },
                text = {
                    Text(text = "Permission is required to access videos.")
                },
                onDismissRequest = {

                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (videoPermissionState.status.isGranted)
                                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(
                                        context
                                    )
                                )
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                else
                                    openGalleryLauncher.launch("video/*")
                            else
                                videoPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = null,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}
