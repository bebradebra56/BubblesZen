package com.bubbzeniac.apssof.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class BubbleState(
    val id: Int,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val colorIndex: Int,
    val shimmerPhase: Float,
    val isPopping: Boolean = false,
    val popProgress: Float = 0f,
)

class BubbleSimulation(
    val width: Float,
    val height: Float,
    private val bubbleCount: Int = 12,
    private val speedMultiplier: Float = 1f,
) {
    var bubbles by mutableStateOf(createInitialBubbles())
        private set

    private var poppedQueue = mutableListOf<Int>()

    private fun createInitialBubbles(): List<BubbleState> {
        return (0 until bubbleCount).map { id ->
            randomBubble(id, width, height, speedMultiplier)
        }
    }

    fun update(deltaSeconds: Float, speedMult: Float = speedMultiplier) {
        val dt = deltaSeconds.coerceIn(0f, 1f / 20f)

        bubbles = bubbles.map { b ->
            if (b.isPopping) {
                val newProgress = b.popProgress + dt * 4f
                if (newProgress >= 1f) {
                    randomBubble(b.id, width, height, speedMult)
                } else {
                    b.copy(popProgress = newProgress)
                }
            } else {
                updateBubble(b, dt, speedMult)
            }
        }
    }

    private fun updateBubble(b: BubbleState, dt: Float, speedMult: Float): BubbleState {
        var nx = b.x + b.vx * dt
        var ny = b.y + b.vy * dt
        var nvx = b.vx
        var nvy = b.vy

        if (nx - b.radius < 0f) { nx = b.radius; nvx = abs(nvx) }
        if (nx + b.radius > width) { nx = width - b.radius; nvx = -abs(nvx) }
        if (ny - b.radius < 0f) { ny = b.radius; nvy = abs(nvy) }
        if (ny + b.radius > height) { ny = height - b.radius; nvy = -abs(nvy) }

        nvx *= 0.998f
        nvy *= 0.998f

        val driftX = (Random.nextFloat() - 0.5f) * 3f * speedMult
        val driftY = (Random.nextFloat() - 0.5f) * 3f * speedMult
        nvx += driftX
        nvy += driftY

        val speed = sqrt(nvx * nvx + nvy * nvy)
        val maxSpeed = 70f * speedMult
        if (speed > maxSpeed) {
            nvx = nvx / speed * maxSpeed
            nvy = nvy / speed * maxSpeed
        }

        val minSpeed = 15f * speedMult
        if (speed < minSpeed && speed > 0f) {
            nvx = nvx / speed * minSpeed
            nvy = nvy / speed * minSpeed
        }

        return b.copy(
            x = nx, y = ny, vx = nvx, vy = nvy,
            shimmerPhase = (b.shimmerPhase + dt * 1.5f) % (2f * PI.toFloat())
        )
    }

    fun onTouch(touchX: Float, touchY: Float): Boolean {
        var popped = false
        bubbles = bubbles.map { b ->
            if (b.isPopping) return@map b
            val dx = b.x - touchX
            val dy = b.y - touchY
            val dist = sqrt(dx * dx + dy * dy)
            when {
                dist < b.radius -> {
                    popped = true
                    b.copy(isPopping = true, popProgress = 0f)
                }
                dist < b.radius * 3.5f -> {
                    val force = (b.radius * 3.5f - dist) / (b.radius * 3.5f) * 250f
                    val nx = if (dist > 0f) dx / dist else 0f
                    val ny = if (dist > 0f) dy / dist else 0f
                    b.copy(vx = b.vx + nx * force, vy = b.vy + ny * force)
                }
                else -> b
            }
        }
        return popped
    }

    fun updateSpeed(speedMult: Float) {
        bubbles = bubbles.map { b ->
            val speed = sqrt(b.vx * b.vx + b.vy * b.vy)
            val targetSpeed = 40f * speedMult
            if (speed > 0f) {
                b.copy(vx = b.vx / speed * targetSpeed, vy = b.vy / speed * targetSpeed)
            } else b
        }
    }

    companion object {
        fun randomBubble(id: Int, width: Float, height: Float, speedMult: Float): BubbleState {
            val r = 35f + Random.nextFloat() * 55f
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val speed = (30f + Random.nextFloat() * 40f) * speedMult
            return BubbleState(
                id = id,
                x = (r + Random.nextFloat() * (width - 2f * r)).coerceIn(r, width - r),
                y = (r + Random.nextFloat() * (height - 2f * r)).coerceIn(r, height - r),
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                radius = r,
                colorIndex = id % 3,
                shimmerPhase = Random.nextFloat() * 2f * PI.toFloat(),
            )
        }
    }
}

fun DrawScope.drawGlossyBubble(
    bubble: BubbleState,
    colors: List<Color>,
) {
    val center = Offset(bubble.x, bubble.y)
    val r = bubble.radius
    val color = colors.getOrElse(bubble.colorIndex) { colors.first() }

    if (bubble.isPopping) {
        val alpha = (1f - bubble.popProgress).coerceIn(0f, 1f)
        val scale = 1f + bubble.popProgress * 0.6f
        val pr = r * scale

        repeat(8) { i ->
            val angle = (i / 8f) * 2f * PI.toFloat() + bubble.shimmerPhase
            val dist = r * (0.5f + bubble.popProgress * 1.2f)
            val px = bubble.x + cos(angle) * dist
            val py = bubble.y + sin(angle) * dist
            val particleR = r * 0.15f * alpha
            drawCircle(
                color = color.copy(alpha = alpha * 0.9f),
                radius = particleR,
                center = Offset(px, py)
            )
        }

        drawCircle(
            color = color.copy(alpha = alpha * 0.3f),
            radius = pr,
            center = center
        )
        drawCircle(
            color = color.copy(alpha = alpha * 0.5f),
            radius = pr,
            center = center,
            style = Stroke(width = 2f)
        )
        return
    }

    val shimmer = (sin(bubble.shimmerPhase.toDouble()) * 0.5 + 0.5).toFloat()

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.55f),
                color.copy(alpha = 0.75f),
            ),
            center = center,
            radius = r
        ),
        radius = r,
        center = center
    )

    val glowAlpha = 0.12f + shimmer * 0.08f
    drawCircle(
        color = color.copy(alpha = glowAlpha),
        radius = r * 1.25f,
        center = center
    )

    val highlightCenter = center + Offset(-r * 0.28f, -r * 0.32f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.85f + shimmer * 0.1f),
                Color.White.copy(alpha = 0.3f),
                Color.Transparent,
            ),
            center = highlightCenter,
            radius = r * 0.42f
        ),
        radius = r * 0.42f,
        center = highlightCenter
    )

    val shine2Center = center + Offset(r * 0.22f, r * 0.28f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f + shimmer * 0.1f),
                Color.Transparent,
            ),
            center = shine2Center,
            radius = r * 0.2f
        ),
        radius = r * 0.2f,
        center = shine2Center
    )

    val rimAlpha = 0.35f + shimmer * 0.15f
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                color.copy(alpha = rimAlpha),
                Color.White.copy(alpha = rimAlpha * 0.8f),
                color.copy(alpha = rimAlpha * 0.6f),
                color.copy(alpha = rimAlpha),
            ),
            center = center
        ),
        radius = r,
        center = center,
        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )
}

fun DrawScope.drawAnimatedBackground(
    phase: Float,
    color1: Color,
    color2: Color,
    color3: Color,
) {
    val wavePx = size.width * 0.5f
    val wavePy = size.height * (0.35f + sin(phase.toDouble()).toFloat() * 0.05f)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(color1, color2, color3),
            startY = 0f,
            endY = size.height
        )
    )

    for (i in 0..2) {
        val cx = size.width * (0.2f + i * 0.3f) + cos((phase + i).toDouble()).toFloat() * 40f
        val cy = size.height * (0.3f + i * 0.2f) + sin((phase * 0.7f + i).toDouble()).toFloat() * 30f
        val radius = size.width * (0.25f + i * 0.05f)
        drawCircle(
            color = color3.copy(alpha = 0.04f),
            radius = radius,
            center = Offset(cx, cy)
        )
    }
}
