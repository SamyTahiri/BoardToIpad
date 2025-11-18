package net.raphdf201.boardtoipad

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BoardToIpad",
    ) {
        App()
    }
}