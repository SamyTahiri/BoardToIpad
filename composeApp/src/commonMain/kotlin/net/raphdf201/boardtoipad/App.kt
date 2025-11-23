package net.raphdf201.boardtoipad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App() {
    var dark by remember { mutableStateOf(true) }
    val textColor = if (dark) Color.White else Color.Black
    var connected by remember { mutableStateOf(false) }
    val api = remember { Api() }
    val views = remember { Views(api) }
    val scope = rememberCoroutineScope()
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color =
            if (dark) Color(30, 36, 48)
            else Color.White
        ) {
            Column(
                Modifier.fillMaxSize().padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.Start
                ) {
                    Button({
                        dark = !dark
                    }) {
                        Text("dark mode", Modifier, textColor)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Connected : $connected", Modifier.align(Alignment.CenterVertically), textColor)
                }
                views.ListThings(scope)
                views.PickleBalls()
            }
        }
    }
}
