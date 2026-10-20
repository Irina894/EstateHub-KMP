package ua.edu.chnu.estatehub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform