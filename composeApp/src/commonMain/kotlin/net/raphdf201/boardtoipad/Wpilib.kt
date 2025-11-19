package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance

var tableInstance: NetworkTableInstance? = null

fun setupNetworkTables(): Boolean {
    if (tableInstance == null) {
        try {
            tableInstance = NetworkTableInstance.getDefault()
        } catch (e: Exception) {
            println("CALIS setupNetworkTables() : ${e.message}")
            return false
        }
    }
    tableInstance!!.startClient("app")
    tableInstance!!.setServer("robot.local")
    return tableInstance!!.isConnected
}
