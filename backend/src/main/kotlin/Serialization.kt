package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.util.*
import java.sql.Connection
import java.sql.DriverManager
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureSerialization() {
        install(ContentNegotiation) {
        json(
            kotlinx.serialization.json.Json {
                prettyPrint = true
            }
        )
    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(mapOf("hello" to "world"))
            }
    }
}
