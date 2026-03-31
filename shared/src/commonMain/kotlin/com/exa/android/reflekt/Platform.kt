package com.exa.android.reflekt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform