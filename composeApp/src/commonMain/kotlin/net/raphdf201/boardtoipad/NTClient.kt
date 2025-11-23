package net.raphdf201.boardtoipad

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromByteArray
import java.util.concurrent.ConcurrentHashMap

class NTClient(private val client: HttpClient) {
    val subscriptions = ConcurrentHashMap<String, Int>()
    val publications = ConcurrentHashMap<String, Int>()
    private val topics = ConcurrentHashMap<Int, TopicInfo>() // topicId -> topic info
    private val topicsByName = ConcurrentHashMap<String, Int>() // name -> topicId
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
        publications[topic] = pubId
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

    /**
     * Process incoming messages from the server
     * Call this in a loop to continuously receive messages
     */
    suspend fun receiveMessage(): NTMessage? {
        val frame = session.incoming.receive()

        return when (frame) {
            is Frame.Text -> handleTextFrame(frame.readText())
            is Frame.Binary -> handleBinaryFrame(frame.data)
            else -> null
        }
    }

    /**
     * Get a flow of all incoming messages
     * More idiomatic Kotlin approach using Flow
     */
    fun messageFlow(): Flow<NTMessage> = flow {
        for (frame in session.incoming) {
            val message = when (frame) {
                is Frame.Text -> handleTextFrame(frame.readText())
                is Frame.Binary -> handleBinaryFrame(frame.data)
                else -> null
            }
            message?.let { emit(it) }
        }
    }

    private fun handleTextFrame(text: String): NTMessage? {
        return try {
            val jsonMessages = Json.decodeFromString<List<JsonMessage>>(text)
            // Process all JSON messages in the frame
            jsonMessages.forEach { msg ->
                when (msg.method) {
                    "announce" -> {
                        val announce = Json.decodeFromString<AnnounceParams>(msg.params.toString())
                        handleAnnounce(announce)
                        return NTMessage.Announce(announce)
                    }
                    "unannounce" -> {
                        val unannounce = Json.decodeFromString<UnannounceParams>(msg.params.toString())
                        handleUnannounce(unannounce)
                        return NTMessage.Unannounce(unannounce)
                    }
                    "properties" -> {
                        val props = Json.decodeFromString<PropertiesParams>(msg.params.toString())
                        return NTMessage.Properties(props)
                    }
                }
            }
            null
        } catch (e: Exception) {
            println("Error parsing text frame: ${e.message}")
            null
        }
    }

    private fun handleBinaryFrame(data: ByteArray): NTMessage? {
        return try {
            // MessagePack binary frame: [topicId, timestamp, dataType, value]
            val array = MsgPack.decodeFromByteArray<List<Any>>(data)

            if (array.size != 4) {
                println("Invalid binary frame size: ${array.size}")
                return null
            }

            val topicId = (array[0] as Number).toInt()
            val timestamp = (array[1] as Number).toLong()
            val dataType = (array[2] as Number).toInt()
            val value = array[3]

            // Special case: RTT measurement (topicId = -1)
            if (topicId == -1) {
                return NTMessage.RTT(timestamp, value)
            }

            val topicInfo = topics[topicId]
            if (topicInfo == null) {
                println("Received value for unknown topic ID: $topicId")
                return null
            }

            NTMessage.Value(
                topicId = topicId,
                topicName = topicInfo.name,
                timestamp = timestamp,
                dataType = dataType,
                value = value
            )
        } catch (e: Exception) {
            println("Error parsing binary frame: ${e.message}")
            null
        }
    }

    private fun handleAnnounce(announce: AnnounceParams) {
        topics[announce.id] = TopicInfo(
            id = announce.id,
            name = announce.name,
            type = announce.type,
            properties = announce.properties
        )
        topicsByName[announce.name] = announce.id
    }

    private fun handleUnannounce(unannounce: UnannounceParams) {
        val topic = topics.remove(unannounce.id)
        topic?.let {
            topicsByName.remove(it.name)
        }
    }

    fun getTopicId(name: String): Int? = topicsByName[name]
    fun getTopicInfo(id: Int): TopicInfo? = topics[id]

    companion object {
        fun produceMessage(method: String, params: String): String {
            return "{\"method\":\"$method\",\"params\":$params}"
        }
    }
}

// Data classes for incoming messages
@Serializable
data class JsonMessage(
    val method: String,
    val params: kotlinx.serialization.json.JsonElement
)

@Serializable
data class AnnounceParams(
    val name: String,
    val id: Int,
    val type: String,
    val pubuid: Int? = null,
    val properties: TopicProperties
)

@Serializable
data class UnannounceParams(
    val name: String,
    val id: Int
)

@Serializable
data class PropertiesParams(
    val name: String,
    val ack: Boolean? = null,
    val update: TopicProperties
)

data class TopicInfo(
    val id: Int,
    val name: String,
    val type: String,
    val properties: TopicProperties
)

// Sealed class for all possible incoming messages
sealed class NTMessage {
    data class Announce(val params: AnnounceParams) : NTMessage()
    data class Unannounce(val params: UnannounceParams) : NTMessage()
    data class Properties(val params: PropertiesParams) : NTMessage()
    data class Value(
        val topicId: Int,
        val topicName: String,
        val timestamp: Long,
        val dataType: Int,
        val value: Any
    ) : NTMessage()
    data class RTT(val serverTime: Long, val clientTime: Any) : NTMessage()
}

// Existing data classes
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
    val cached: Boolean = true
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
