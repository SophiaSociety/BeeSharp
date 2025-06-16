package com.beesharp.backend

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.Database

import com.beesharp.backend.config.configureSerialization
import com.beesharp.backend.config.configureSecurity
import com.beesharp.backend.config.configureTemplating

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        anyHost() // Para desenvolvimento. Em produção, especifique os domínios permitidos!
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
    }
    configureSerialization()
    configureSecurity()
    configureTemplating()
    configureDatabases()
    configureRouting()
}

fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/beesharp",
        driver = "org.postgresql.Driver",
        user = "beesharp_user",
        password = "supersecret"
    )
}
