package net.raphdf201.boardtoipad

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform