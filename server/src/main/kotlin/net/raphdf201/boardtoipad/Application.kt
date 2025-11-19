package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
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
            get("/raw") {
                call.respondBytes(networkTablesInstance.getTable(call.queryParameters["table"]).getValue(call.queryParameters["value"]).raw)
            }
            get("/double") {
                call.respondText(networkTablesInstance.getTable(call.queryParameters["table"]).getValue(call.queryParameters["value"]).double.toString())
            }
            get("/string") {
                call.respondText(networkTablesInstance.getTable(call.queryParameters["table"]).getValue(call.queryParameters["value"]).string.toString())
            }
            get("/dynamic") {
                val chose = networkTablesInstance.getTable(call.queryParameters["table"]).getValue(call.queryParameters["value"])
                chose.type
                call.respondText()
            }
        }
    }
}
