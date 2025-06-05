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

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        
        // Rota dinâmica para o perfil do usuário
        get("/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, mapOf("error" to "Username not provided")
            )

            val dbConnection = connectToPostgres(embedded = false)

            // Consultar informações do usuário no banco de dados
            val userInfo = dbConnection.prepareStatement(
                "SELECT id, email FROM users WHERE username = ?"
            ).use { statement ->
                statement.setString(1, username)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    mapOf(
                        "id" to resultSet.getInt("id"),
                        "email" to resultSet.getString("email")
                    )
                } else {
                    null
                }
            }

            if (userInfo == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@get
            }

            val userId = userInfo["id"] as Int

            // Consultar álbuns favoritos do usuário
            val albums = dbConnection.prepareStatement(
                "SELECT album_id1, album_id2, album_id3, album_id4 FROM albumfavorites WHERE user_id = ?"
            ).use { statement ->
                statement.setInt(1, userId)
                val resultSet = statement.executeQuery()
                val albumList = mutableListOf<Int>()
                if (resultSet.next()) {
                    val ids = listOf(
                        resultSet.getInt("album_id1").takeIf { !resultSet.wasNull() },
                        resultSet.getInt("album_id2").takeIf { !resultSet.wasNull() },
                        resultSet.getInt("album_id3").takeIf { !resultSet.wasNull() },
                        resultSet.getInt("album_id4").takeIf { !resultSet.wasNull() }
                    )
                    albumList.addAll(ids.filterNotNull())
                }
                albumList
            }

            // Consultar lista de pessoas que o usuário segue
            val following = dbConnection.prepareStatement(
                "SELECT u.username FROM userfollows uf JOIN users u ON uf.follower_id = u.id WHERE uf.user_id = ?"
            ).use { statement ->
                statement.setInt(1, userId)
                val resultSet = statement.executeQuery()
                val followingList = mutableListOf<String>()
                while (resultSet.next()) {
                    followingList.add(resultSet.getString("username"))
                }
                followingList
            }

            // Responder com JSON
            call.respond(
                mapOf(
                    "username" to username,
                    "email" to userInfo["email"],
                    "albums" to albums,
                    "following" to following
                )
            )
        }
    }
}
