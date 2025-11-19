package net.raphdf201.boardtoipad

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class Api {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    fun skibidi() {

    }
}
