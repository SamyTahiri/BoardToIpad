package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableInstance

val tableInstance = NetworkTableInstance.getDefault()!!

fun setupTable() {
    tableInstance.startClient("app")
    tableInstance.setServer("robot.local")
}
