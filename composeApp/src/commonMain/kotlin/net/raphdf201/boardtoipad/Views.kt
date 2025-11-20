package net.raphdf201.boardtoipad

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Views(val api: Api) {
    @Composable
    fun PickleBalls() {
        Box(
            Modifier
                .fillMaxSize()
                .absolutePadding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceEvenly,
                Alignment.Bottom
            ) {
                LonelyRobot(IntakeType.BIDIRECTIONAL)
                LonelyRobot(IntakeType.UP_DOWN)
                LonelyRobot(IntakeType.SINGLE)
            }
        }
    }

    enum class IntakeType {
        BIDIRECTIONAL, UP_DOWN, SINGLE
    }

    @Composable
    private fun LonelyRobot(type: IntakeType) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.92f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "scale"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    println("clicked $type") // TODO : smartdashboard
                }
        ) {
            // Direction indicator (arrows)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(40.dp)
            ) {
                DirectionIndicator(type)
            }

            // Motor wheel
            MotorWheel()

            // Funnel
            FunnelShape()

            // Storage container (robot)
            Surface(
                modifier = Modifier.size(120.dp, 110.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF8B9299),
                shadowElevation = if (isPressed) 2.dp else 4.dp
            ) {}
        }
    }

    @Composable
    private fun MotorWheel() {
        val infiniteTransition = rememberInfiniteTransition(label = "motor")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Surface(
            modifier = Modifier
                .size(48.dp)
                .rotate(rotation),
            shape = CircleShape,
            color = Color(0xFFFCD34D),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    // Center hole
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = 3.5.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )

                    // Inner ring of holes (6 holes)
                    val innerRadius = 10.dp.toPx()
                    val innerHoleSize = 3.dp.toPx()
                    for (i in 0..5) {
                        val angle = (i * 60f) * (Math.PI / 180f)
                        val x = centerX + (innerRadius * kotlin.math.cos(angle)).toFloat()
                        val y = centerY + (innerRadius * kotlin.math.sin(angle)).toFloat()
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = innerHoleSize,
                            center = Offset(x, y)
                        )
                    }

                    // Outer ring of holes (6 holes, offset from inner)
                    val outerRadius = 17.dp.toPx()
                    val outerHoleSize = 3.dp.toPx()
                    for (i in 0..5) {
                        val angle = ((i * 60f) + 30f) * (Math.PI / 180f) // Offset by 30 degrees
                        val x = centerX + (outerRadius * kotlin.math.cos(angle)).toFloat()
                        val y = centerY + (outerRadius * kotlin.math.sin(angle)).toFloat()
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = outerHoleSize,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun DirectionIndicator(type: IntakeType) {
        when (type) {
            IntakeType.BIDIRECTIONAL -> BidirectionalArrows()
            IntakeType.UP_DOWN -> UpDownArrows()
            IntakeType.SINGLE -> Spacer(modifier = Modifier.size(1.dp))
        }
    }

    @Composable
    private fun BidirectionalArrows() {
        Canvas(modifier = Modifier.size(100.dp, 70.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val arrowColor = Color(0xFFFCD34D)
            val strokeWidth = 3.5.dp.toPx()
            val arrowLength = 25.dp.toPx()

            // Left arrow line
            drawLine(
                color = arrowColor,
                start = Offset(centerX - arrowLength, centerY),
                end = Offset(centerX - 5.dp.toPx(), centerY),
                strokeWidth = strokeWidth
            )

            // Left arrowhead
            val leftArrowPath = Path().apply {
                moveTo(centerX - arrowLength + 7.dp.toPx(), centerY - 8.dp.toPx())
                lineTo(centerX - arrowLength, centerY)
                lineTo(centerX - arrowLength + 7.dp.toPx(), centerY + 8.dp.toPx())
            }
            drawPath(leftArrowPath, arrowColor, style = Stroke(width = strokeWidth))

            // Right arrow line
            drawLine(
                color = arrowColor,
                start = Offset(centerX + 5.dp.toPx(), centerY),
                end = Offset(centerX + arrowLength, centerY),
                strokeWidth = strokeWidth
            )

            // Right arrowhead
            val rightArrowPath = Path().apply {
                moveTo(centerX + arrowLength - 7.dp.toPx(), centerY - 8.dp.toPx())
                lineTo(centerX + arrowLength, centerY)
                lineTo(centerX + arrowLength - 7.dp.toPx(), centerY + 8.dp.toPx())
            }
            drawPath(rightArrowPath, arrowColor, style = Stroke(width = strokeWidth))
        }
    }

    @Composable
    private fun UpDownArrows() {
        Canvas(modifier = Modifier.size(50.dp, 75.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val arrowColor = Color(0xFF60A5FA)
            val strokeWidth = 3.5.dp.toPx()
            val arrowLength = 25.dp.toPx()

            // Vertical line through center
            drawLine(
                color = arrowColor,
                start = Offset(centerX, centerY - arrowLength),
                end = Offset(centerX, centerY + arrowLength),
                strokeWidth = strokeWidth
            )

            // Down arrowhead (pointing down)
            val downArrowPath = Path().apply {
                moveTo(centerX - 8.dp.toPx(), centerY - arrowLength + 7.dp.toPx())
                lineTo(centerX, centerY - arrowLength)
                lineTo(centerX + 8.dp.toPx(), centerY - arrowLength + 7.dp.toPx())
            }
            drawPath(downArrowPath, arrowColor, style = Stroke(width = strokeWidth))

            // Down arrowhead (pointing down at bottom)
            val downArrowPath2 = Path().apply {
                moveTo(centerX - 8.dp.toPx(), centerY + arrowLength - 7.dp.toPx())
                lineTo(centerX, centerY + arrowLength)
                lineTo(centerX + 8.dp.toPx(), centerY + arrowLength - 7.dp.toPx())
            }
            drawPath(downArrowPath2, arrowColor, style = Stroke(width = strokeWidth))
        }
    }

    @Composable
    private fun FunnelShape() {
        Canvas(modifier = Modifier.size(40.dp, 28.dp)) {
            val funnelPath = Path().apply {
                moveTo(size.width * 0.3f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(size.width * 0.7f, 0f)
                close()
            }
            drawPath(funnelPath, Color(0xFF9CA3AF))
        }
    }

    @Composable
    fun ListThings(scope: CoroutineScope) {
        var t by remember { mutableStateOf<Table?>(null) }
        Column {
            Button({
                scope.launch {
                    t = api.list("smartdashboard")
                }
            }) {
                Text("ls")
            }
            LazyColumn {
                item {
                    Text("Keys :")
                }
                items(t?.keys?.toList() ?: emptyList()) {
                    Text(it)
                }
                item {
                    Text("Subtables :")
                }
                items(t?.subTables?.toList() ?: emptyList()) {
                    Text(it)
                }
            }
        }
    }
}
