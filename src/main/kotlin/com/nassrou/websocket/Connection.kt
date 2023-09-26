package com.nassrou.websocket

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class Connection(
    val id: Int,
    val userId: Int = 0,
    var session: DefaultWebSocketSession? = null
)