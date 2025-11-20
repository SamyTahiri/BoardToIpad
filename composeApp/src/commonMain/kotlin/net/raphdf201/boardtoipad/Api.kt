package net.raphdf201.boardtoipad

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json

class Api {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getDynamic(table: String, value: String): DynamicResponse? {
        val req = httpClient.post {
            url {
                host = robot
                path("/nt4/dynamic")
            }
            setBody(TableDescription(table, value))
        }
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }

    suspend fun getString(table: String, value: String): StringResponse? {
        val req = httpClient.post {
            url {
                host = robot
                path("/nt4/string")
            }
            setBody(TableDescription(table, value))
        }
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }

    suspend fun getDouble(table: String, value: String): DoubleResponse? {
        val req = httpClient.post {
            url {
                host = robot
                path("/nt4/double")
            }
            setBody(TableDescription(table, value))
        }
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }

    suspend fun list(table: String): Table? {
        val req = httpClient.get {
            url {
                host = robot
                path("/nt4/$table")
            }
        }
        if (req.status == HttpStatusCode.NoContent) return null
        return req.body()
    }
}
