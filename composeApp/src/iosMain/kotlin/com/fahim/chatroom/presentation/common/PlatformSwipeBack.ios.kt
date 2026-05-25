package com.fahim.chatroom.presentation.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val EdgeStartThreshold = 24.dp
private val CommitDistance = 80.dp
private const val HorizontalDominanceRatio = 1.5f

actual fun Modifier.platformSwipeBack(enabled: Boolean, onBack: () -> Unit): Modifier {
    if (!enabled) return this
    return this.pointerInput(onBack) {
        val edgePx = EdgeStartThreshold.toPx()
        val commitPx = CommitDistance.toPx()
        awaitEachGesture {
            // Observe the down before children so edge-swipes win over inner tap targets.
            val first = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (first.position.x > edgePx) return@awaitEachGesture

            var totalDx = 0f
            var totalDy = 0f
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == first.id } ?: break
                val delta = change.positionChange()
                totalDx += delta.x
                totalDy += delta.y
                // Bail once the gesture turns vertical — it's a scroll, not a back swipe.
                if (abs(totalDy) > abs(totalDx) * HorizontalDominanceRatio) break
                if (totalDx >= commitPx) {
                    change.consume()
                    onBack()
                    break
                }
                if (!change.pressed) break
            }
        }
    }
}
