package com.fahim.chatroom.presentation.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lightweight vector icons drawn with Canvas. Pure Compose Multiplatform — no extra deps.
 * Sized intentionally small; tint defaults to the current onSurface ink.
 */

private val DefaultIconSize: Dp = 20.dp

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.11f).coerceAtLeast(1.5f)
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = s * 0.28f
        drawLine(tint, Offset(cx + r * 0.5f, cy - r), Offset(cx - r * 0.5f, cy), stroke, StrokeCap.Round)
        drawLine(tint, Offset(cx - r * 0.5f, cy), Offset(cx + r * 0.5f, cy + r), stroke, StrokeCap.Round)
    }
}

@Composable
fun PlusIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.13f).coerceAtLeast(2f)
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = s * 0.28f
        drawLine(tint, Offset(cx - r, cy), Offset(cx + r, cy), stroke, StrokeCap.Round)
        drawLine(tint, Offset(cx, cy - r), Offset(cx, cy + r), stroke, StrokeCap.Round)
    }
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    tint: Color = Color.White,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.12f).coerceAtLeast(2f)
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.82f, h * 0.50f)
            lineTo(w * 0.18f, h * 0.18f)
            lineTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.18f, h * 0.82f)
            close()
        }
        drawPath(
            path,
            color = tint,
            style = Stroke(width = stroke, join = StrokeJoin.Round, cap = StrokeCap.Round),
        )
        drawLine(
            tint,
            Offset(w * 0.38f, h * 0.50f),
            Offset(w * 0.82f, h * 0.50f),
            stroke,
            StrokeCap.Round,
        )
    }
}

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.16f).coerceAtLeast(1.5f)
        val w = this.size.width
        val h = this.size.height
        drawLine(tint, Offset(w * 0.18f, h * 0.55f), Offset(w * 0.42f, h * 0.78f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.42f, h * 0.78f), Offset(w * 0.85f, h * 0.28f), stroke, StrokeCap.Round)
    }
}

@Composable
fun ChevronDownIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.11f).coerceAtLeast(1.5f)
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = s * 0.26f
        drawLine(tint, Offset(cx - r, cy - r * 0.5f), Offset(cx, cy + r * 0.5f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(cx, cy + r * 0.5f), Offset(cx + r, cy - r * 0.5f), stroke, StrokeCap.Round)
    }
}

@Composable
fun ChevronRightIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.11f).coerceAtLeast(1.5f)
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = s * 0.26f
        drawLine(tint, Offset(cx - r * 0.5f, cy - r), Offset(cx + r * 0.5f, cy), stroke, StrokeCap.Round)
        drawLine(tint, Offset(cx + r * 0.5f, cy), Offset(cx - r * 0.5f, cy + r), stroke, StrokeCap.Round)
    }
}

@Composable
fun AlertDotIcon(
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
    tint: Color = MaterialTheme.colorScheme.error,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        drawCircle(color = tint, radius = s * 0.45f, center = Offset(this.size.width / 2f, this.size.height / 2f))
        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = s * 0.08f,
            center = Offset(this.size.width / 2f, this.size.height * 0.66f),
        )
        val barTop = this.size.height * 0.28f
        val barBottom = this.size.height * 0.55f
        drawLine(
            color = Color.White.copy(alpha = 0.95f),
            start = Offset(this.size.width / 2f, barTop),
            end = Offset(this.size.width / 2f, barBottom),
            strokeWidth = s * 0.13f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun LockBadgeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val w = this.size.width
        val h = this.size.height
        val stroke = (s * 0.14f).coerceAtLeast(1f)
        // shackle
        val arcTop = h * 0.10f
        val arcLeft = w * 0.28f
        val arcRight = w * 0.72f
        drawLine(tint, Offset(arcLeft, arcTop + h * 0.20f), Offset(arcLeft, h * 0.45f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(arcRight, arcTop + h * 0.20f), Offset(arcRight, h * 0.45f), stroke, StrokeCap.Round)
        val path = Path().apply {
            moveTo(arcLeft, arcTop + h * 0.25f)
            cubicTo(
                arcLeft, arcTop - h * 0.05f,
                arcRight, arcTop - h * 0.05f,
                arcRight, arcTop + h * 0.25f,
            )
        }
        drawPath(path, tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // body
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.18f, h * 0.45f),
            size = Size(w * 0.64f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(s * 0.10f, s * 0.10f),
        )
    }
}

@Composable
fun UsersIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = (s * 0.10f).coerceAtLeast(1.5f)
        val w = this.size.width
        val h = this.size.height

        // User 1 (background / right / smaller)
        val cx1 = w * 0.68f
        val cy1 = h * 0.38f
        val r1 = s * 0.14f
        drawCircle(
            color = tint,
            radius = r1 - stroke / 2,
            center = Offset(cx1, cy1),
            style = Stroke(width = stroke)
        )
        val path1 = Path().apply {
            moveTo(w * 0.48f, h * 0.72f)
            quadraticTo(w * 0.48f, h * 0.55f, w * 0.68f, h * 0.55f)
            quadraticTo(w * 0.88f, h * 0.55f, w * 0.88f, h * 0.72f)
        }
        drawPath(path1, tint, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // User 2 (foreground / left / larger)
        val cx2 = w * 0.32f
        val cy2 = h * 0.44f
        val r2 = s * 0.17f
        drawCircle(
            color = tint,
            radius = r2 - stroke / 2,
            center = Offset(cx2, cy2),
            style = Stroke(width = stroke)
        )
        val path2 = Path().apply {
            moveTo(w * 0.10f, h * 0.82f)
            quadraticTo(w * 0.10f, h * 0.62f, w * 0.32f, h * 0.62f)
            quadraticTo(w * 0.54f, h * 0.62f, w * 0.54f, h * 0.82f)
        }
        drawPath(path2, tint, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}
