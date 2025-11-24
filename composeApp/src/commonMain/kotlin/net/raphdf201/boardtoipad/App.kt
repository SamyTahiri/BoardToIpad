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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App() {
    var dark by remember { mutableStateOf(true) }
    var cinfo by remember { mutableStateOf("") }
    var startedPeriodic by remember { mutableStateOf(false) }
    val nt = remember { NTClient(HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets) {
            pingInterval = 1000.milliseconds
        }
    }) }
    val views = remember { Views(nt) }
    val scope = rememberCoroutineScope()
    val connected = nt.connected.collectAsState()
    var currentMsg by remember { mutableStateOf<NTMessage?>(null) }
    if (!startedPeriodic) LaunchedEffect(Unit) {
        while (isActive) {
            try {
                currentMsg = nt.receiveMessage()
            } catch (_: Exception) {
            }
            delay(50)
        }
    }
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color =
            Color(30, 36, 48)
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
                        Text("dark mode", Modifier, Color.White)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button({
                        println("connecting")
                        scope.launch {
                            nt.connect()
                        }
                    }) {
                        Text("Connected : ${connected.value}", Modifier.align(Alignment.CenterVertically), Color.White)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button({
                        println("getting conn info")
                        cinfo = try {
                            "ConnInfo : ${nt.getConnInfo()}"
                        } catch (_: Exception) {
                            ""
                        }
                    }) {
                        Text(cinfo, Modifier, Color.White)
                    }
                }
                views.ListThings(currentMsg)
                views.PickleBalls()
            }
        }
    }
}
