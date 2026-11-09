package com.bodnar.carpathians

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bodnar.carpathians.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Carpathians",
            state = rememberWindowState(width = 430.dp, height = 880.dp),
        ) {
            App()
        }
    }
}
