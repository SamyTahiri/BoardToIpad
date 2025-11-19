plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "net.raphdf201.boardtoipad"
version = "1.0.0"
application {
    mainClass.set("net.raphdf201.boardtoipad.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.wpilib.wpilibj)
    implementation(libs.wpilib.wpiunits)
    implementation(libs.wpilib.wpiutil)
    implementation(libs.wpilib.ntcore)
    implementation(libs.wpilib.hal)
}