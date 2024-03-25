package com.romreviewer.videoeditorcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.romreviewer.videoeditorcompose.ui.home.HomeScreen
import com.romreviewer.videoeditorcompose.ui.theme.VideoEditorComposeTheme
import com.romreviewer.videoeditorcompose.ui.videoeditor.VideoEditorScreen

private const val TWEEN_DURATION = 300

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoEditorComposeTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(TWEEN_DURATION)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(TWEEN_DURATION)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(TWEEN_DURATION)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(TWEEN_DURATION)
                        )
                    },
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(navController)
                    }
                    composable(
                        "video_editor/{uri}",
                        arguments = listOf(navArgument("uri") { type = NavType.StringType })
                    ) {
                        VideoEditorScreen(viewModel = viewModel(),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
