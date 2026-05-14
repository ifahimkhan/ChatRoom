package com.fahim.chatroom

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

private var koinStarted = false

fun MainViewController(): UIViewController {
    if (!koinStarted) {
        initKoinIos()
        koinStarted = true
    }
    return ComposeUIViewController { App() }
}