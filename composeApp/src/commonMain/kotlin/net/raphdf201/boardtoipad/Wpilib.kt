package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance

var tableInstance: NetworkTableInstance? = null

fun setupNetworkTables(): Boolean {
    if (tableInstance == null) {
        tableInstance = NetworkTableInstance.getDefault()
    }
    if (tableInstance == null) {
        return false
    }
    tableInstance!!.startClient("app")
    tableInstance!!.setServer("robot.local")
    return tableInstance!!.isConnected
}
