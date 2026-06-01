package com.example.medireminder.features.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medireminder.ui.theme.PrimaryGreen
import com.example.medireminder.ui.theme.SecondaryGreen
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.6f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate logo in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        // Animate tagline
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, delayMillis = 200)
        )
        // Wait a bit then proceed
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Pill + Clock Icon
            PillClockIcon(
                modifier = Modifier.size(140.dp),
                alpha = alpha.value,
                scale = scale.value
            )

            Spacer(modifier = Modifier.height(32.dp))

            // App Title
            Text(
                text = "MediReminder",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Manage Medicine & Stock Manager",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline with fade-in animation
            Text(
                text = "Stay on track. Stay healthy.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = SecondaryGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}

@Composable
private fun PillClockIcon(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    scale: Float = 1f
) {
    val pillColor = PrimaryGreen
    val clockColor = Color(0xFF4C8C4A)

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f

        // Draw clock outer rings
        val clockRadius = (canvasWidth * 0.38f) * scale
        drawCircle(
            color = clockColor.copy(alpha = alpha * 0.15f),
            radius = clockRadius,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = clockColor.copy(alpha = alpha * 0.3f),
            radius = clockRadius * 0.85f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = clockColor.copy(alpha = alpha),
            style = Stroke(width = 3f * scale),
            radius = clockRadius * 0.7f,
            center = Offset(centerX, centerY)
        )

        // Clock hour markers (12, 3, 6, 9)
        val markerRadius = clockRadius * 0.65f
        val markerLength = clockRadius * 0.12f
        for (angleDeg in listOf(0, 90, 180, 270)) {
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val startX = centerX + markerRadius * cos(angleRad).toFloat()
            val startY = centerY + markerRadius * sin(angleRad).toFloat()
            val endX = centerX + (markerRadius + markerLength) * cos(angleRad).toFloat()
            val endY = centerY + (markerRadius + markerLength) * sin(angleRad).toFloat()
            drawLine(
                color = pillColor.copy(alpha = alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 3f * scale,
                cap = StrokeCap.Round
            )
        }

        // Clock hour hand (pointing to ~2 o'clock)
        val hourAngleDeg = 60.0
        val hourLength = clockRadius * 0.35f
        val hourRad = Math.toRadians(hourAngleDeg - 90)
        drawLine(
            color = pillColor.copy(alpha = alpha),
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + hourLength * cos(hourRad).toFloat(),
                centerY + hourLength * sin(hourRad).toFloat()
            ),
            strokeWidth = 4f * scale,
            cap = StrokeCap.Round
        )

        // Clock minute hand (pointing to ~12 o'clock)
        val minAngleDeg = -90.0
        val minLength = clockRadius * 0.5f
        val minRad = Math.toRadians(minAngleDeg)
        drawLine(
            color = pillColor.copy(alpha = alpha),
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + minLength * cos(minRad).toFloat(),
                centerY + minLength * sin(minRad).toFloat()
            ),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )

        // Clock center dot
        drawCircle(
            color = pillColor.copy(alpha = alpha),
            radius = 4f * scale,
            center = Offset(centerX, centerY)
        )

        // Pill shape on the right side of the clock
        val pillTopY = centerY - clockRadius * 0.8f
        val pillBottomY = centerY + clockRadius * 0.8f
        val pillLeftX = centerX + clockRadius * 0.5f
        val pillRightX = pillLeftX + clockRadius * 0.45f
        val pillWidth = pillRightX - pillLeftX
        val pillHeight = pillBottomY - pillTopY
        val pillCornerRadius = pillWidth / 2f

        // Pill body
        drawRoundRect(
            color = pillColor.copy(alpha = alpha * 0.9f),
            topLeft = Offset(pillLeftX, pillTopY),
            size = Size(pillWidth, pillHeight),
            cornerRadius = CornerRadius(pillCornerRadius, pillCornerRadius)
        )

        // Pill highlight band (white stripe in the middle)
        val bandWidth = pillWidth * 0.5f
        val bandHeight = pillHeight * 0.15f
        drawRoundRect(
            color = Color.White.copy(alpha = alpha * 0.5f),
            topLeft = Offset(
                pillLeftX + (pillWidth - bandWidth) / 2f,
                pillTopY + pillHeight * 0.42f
            ),
            size = Size(bandWidth, bandHeight),
            cornerRadius = CornerRadius(bandHeight / 2f, bandHeight / 2f)
        )
    }
}
