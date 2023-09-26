package com.nassrou.websocket

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

fun Route.socket() {
    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    webSocket("/echo") {
        send("Enter your fucking name:")
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            if (receivedText.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client Left hh"))
            } else {
                send(Frame.Text("Hi, $receivedText"))
            }
        }
    }

    webSocket("/chat/{id}/{userId}") {
        val userId = this.call.parameters["userId"].toString().toInt()
        val id = this.call.parameters["id"].toString().toInt()
        val thisConnection = Connection(id, userId, this)
        if (!connections.contains(thisConnection)) {
            connections += thisConnection
            print(connections.toString())
        }
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val message = frame.readText()
                connections.forEach {
                    if (it.id == id) {
                        it.session?.send(message)
                    }
                }
            }
        } catch (e: Exception) {
            print(e.localizedMessage)
        } finally {
            connections -= thisConnection
        }
    }
}