package net.raphdf201.boardtoipad

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.send
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class NTClient(private val client: HttpClient) {
    val subscriptions = ConcurrentHashMap<String, Int>()
    val publications = ConcurrentHashMap<String, Int>()
    private var subId = 0
    private var pubId = 0
    private lateinit var session: DefaultClientWebSocketSession
    suspend fun connect() {
        session = client.webSocketSession(
            host = robot,
            port = port,
            path = "/nt/kt"
        ) {
            header("Sec-WebSocket-Protocol", "v4.1.networktables.first.wpi.edu")
        }
    }

    suspend fun subscribe(topic: String) {
        subscriptions[topic] = subId
        session.sendMessage(
            "subscribe",
            SubscribeMessage(listOf(topic), subId, SubscriptionOptions())
        )
        subId++
    }

    suspend fun unsubscribe(topic: String) {
        val id = subscriptions[topic] ?: return
        session.sendMessage(
            "unsubscribe",
            UnsubscribeMessage(id)
        )
        subscriptions.remove(topic)
    }

    suspend fun publish(topic: String, type: NTType) {
        subscriptions[topic] ?: return
        session.sendMessage(
            "publish",
            PublishMessage(topic, pubId, type.str, TopicProperties())
        )
        pubId++
    }

    suspend fun unpublish(topic: String) {
        val id = publications[topic] ?: return
        session.sendMessage(
            "unpublish",
            UnpublishMessage(id)
        )
        publications.remove(topic)
    }

    suspend fun setproperties(topic: String, properties: TopicProperties) {
        session.sendMessage("setproperties", SetpropertiesMessage(topic, properties))
    }

    companion object {
        fun produceMessage(method: String, params: String): String {
            return "{\"method\":\"$method\",\"params\":\"$params\"}"
        }
    }
}

@Serializable
data class SubscribeMessage(
    val topics: List<String>,
    val subuid: Int,
    val options: SubscriptionOptions
)

@Serializable
data class UnsubscribeMessage(
    val subuid: Int
)

@Serializable
data class SubscriptionOptions(
    val periodic: Double = 0.1,
    val all: Boolean = false,
    val topicsonly: Boolean = false,
    val prefix: Boolean = false
)

@Serializable
data class PublishMessage(
    val name: String,
    val pubuid: Int,
    val type: String,
    val properties: TopicProperties
)

@Serializable
data class UnpublishMessage(
    val pubuid: Int
)

@Serializable
data class SetpropertiesMessage(
    val name: String,
    val update: TopicProperties
)

@Serializable
data class TopicProperties(
    val persistent: Boolean = false,
    val retained: Boolean = false,
    val cached: Boolean = false
)

@Serializable
data class AnnounceMessage(
    val name: String,
    val id: Int,
    val type: String,
    val pubuid: Int?,
    val properties: TopicProperties
)

@Serializable
data class UnannounceMessage(
    val name: String,
    val id: Int
)

@Serializable
data class PropertiesMessage(
    val name: String,
    val ack: Boolean?
)

@Serializable
data class BinFrame(
    val topicId: Int,
    val timestamp: Int,
    val type: Int,
    val value: List<Byte>
)

enum class NTType(val str: String, val id: Int) {
    BOOLEAN("boolean", 0),
    DOUBLE("double", 1),
    STRING("string", 4),
    JSON("json", 4),
    INT("int", 2),
    FLOAT("float", 3),
    RAW("raw", 5),
    RPC("rpc", 5),
    MSGPACK("msgpack", 5),
    PROTOBUF("protobuf", 5),
    BOOLEANARR("boolean[]", 16),
    DOUBLEARR("double[]", 17),
    STRINGARR("string[]", 20),
    INTARR("int[]", 18),
    FLOATARR("float[]", 19);
}

suspend inline fun <reified T> DefaultClientWebSocketSession.sendMessage(
    method: String,
    params: T
) = this.send(NTClient.produceMessage(method, Json.encodeToString(params)))
