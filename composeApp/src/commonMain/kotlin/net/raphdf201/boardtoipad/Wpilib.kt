package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance

var tableInstance: NetworkTableInstance? = null

fun setupTable() {
    tableInstance = NetworkTableInstance.getDefault()
    tableInstance?.startClient("app")
    tableInstance?.setServer("robot.local")
}
