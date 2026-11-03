package com.bodnar.carpathians

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform