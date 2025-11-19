package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.NetworkTableValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

lateinit var networkTablesInstance: NetworkTableInstance

fun main() {
    embeddedServer(CIO, 6967, module = Application::module).start(true)
    networkTablesInstance = NetworkTableInstance.getDefault()
}

fun Application.module() {
    routing {
        route("/nt4") {
            get("/dynamic") {
                val chose: NetworkTableValue? = networkTablesInstance.getTable(call.queryParameters["table"]).getValue(call.queryParameters["value"])
                if (chose == null) {
                    call.respond(HttpStatusCode.NoContent)
                    return@get
                }
                call.respond(DynamicResponse(chose.type.toKtorType(), chose.value))
            }
        }
    }
}
