package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent")

        get("/") {
            call.respondText("Hello World!")
        }
    }
}
