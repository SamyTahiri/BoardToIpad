package net.raphdf201.boardtoipad

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json

class Api {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getDynamic(): DynamicResponse? {
        val req = httpClient.get("/nt4/dynamic")
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }

    suspend fun getString(): StringResponse? {
        val req = httpClient.get("/nt4/string")
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }

    suspend fun getDouble(): DoubleResponse? {
        val req = httpClient.get("/nt4/double")
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }
}
