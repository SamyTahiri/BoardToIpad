package net.raphdf201.boardtoipad

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import boardtoipad.composeapp.generated.resources.Res
import boardtoipad.composeapp.generated.resources.pickleball
import org.jetbrains.compose.resources.painterResource

@Composable
fun Robots() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        val pickleball = painterResource(Res.drawable.pickleball)
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
            if (isPressed) .85f else 1f,
            spring(
                Spring.DampingRatioHighBouncy,
                Spring.StiffnessHigh
            )
        )

        Image(
            pickleball,
            "skibidi pickleball",
            Modifier
                .scale(scale)
                .clickable(
                    interactionSource,
                    null
                ) {
                    println("clicked ball") // TODO : smartdashboard
                }
        )
        Image(pickleball, null)
        Image(pickleball, null)
    }
}