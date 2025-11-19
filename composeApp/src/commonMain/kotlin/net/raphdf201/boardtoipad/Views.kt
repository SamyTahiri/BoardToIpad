package net.raphdf201.boardtoipad

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import boardtoipad.composeapp.generated.resources.Res
import boardtoipad.composeapp.generated.resources.arrow
import boardtoipad.composeapp.generated.resources.ball
import boardtoipad.composeapp.generated.resources.ball2
import boardtoipad.composeapp.generated.resources.robot
import boardtoipad.composeapp.generated.resources.rotBall
import org.jetbrains.compose.resources.painterResource

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
            LonelyRobot {
                Image(painterResource(Res.drawable.rotBall),
                    null,
                    Modifier
                        .scale(1.8f)
                        .offset(33.dp, -30.dp)
                )
            }
            LonelyRobot {
                Image(painterResource(Res.drawable.arrow),
                    null,
                    Modifier
                        .scale(1.8f)
                        .offset(40.dp, -30.dp)
                )
            }
            LonelyRobot {

                Image(painterResource(Res.drawable.ball2),
                    null,
                    Modifier
                        .scale(1.8f)
                        .offset(42.dp, -5.dp)
                )
            }
        }
    }
}

@Composable
fun LonelyRobot(topImage: @Composable () -> Unit) {
    val camera = painterResource(Res.drawable.robot)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        if (isPressed) .95f else 1f,
        spring(
            Spring.DampingRatioHighBouncy,
            Spring.StiffnessHigh
        )
    )

    Column {
        topImage()
        Image(
            camera,
            "skibidi pickleball",
            Modifier
                .scale(scale)
                .clickable(
                    interactionSource,
                    null
                ) {
                    println("clicked ball") // TODO : smartdashboard
                },
        )
    }
}
