package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.NetworkTableValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route

lateinit var networkTablesInstance: NetworkTableInstance

fun main() {
    embeddedServer(CIO, 6967, module = Application::module).start(true)
    networkTablesInstance = NetworkTableInstance.getDefault()
    networkTablesInstance.setServer("localhost")
    networkTablesInstance.startClient("skibidiTablet")
}

fun Application.module() {
    routing {
        route("/nt4") {
            post("/dynamic") {
                val tb: TableDescription = call.receive()
                val ntValue: NetworkTableValue? = networkTablesInstance.getTable(tb.table).getValue(tb.value)
                if (ntValue == null) {
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }
                call.respond(DynamicResponse(ntValue.type.toKtorType(), ntValue.value))
            }
            post("/string") {
                val tb: TableDescription = call.receive()
                val ntValue: NetworkTableValue? = networkTablesInstance.getTable(tb.table).getValue(tb.value)
                if (ntValue == null || !ntValue.isString) {
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }
                call.respond(StringResponse(ntValue.string))
            }
            post("/double") {
                val tb: TableDescription = call.receive()
                val ntValue: NetworkTableValue? = networkTablesInstance.getTable(tb.table).getValue(tb.value)
                if (ntValue == null || !ntValue.isDouble) {
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }
                call.respond(DoubleResponse(ntValue.double))
            }
            get("/list/{table}") {
                val tb = networkTablesInstance.getTable(call.parameters["table"])
                if (tb == null) {
                    call.respond(HttpStatusCode.NoContent)
                    return@get
                }
                call.respond(Table(tb.keys, tb.subTables))
            }
        }
    }
}
