package com.beesharp.backend

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

import com.beesharp.backend.repository.UserRepository
import com.beesharp.backend.repository.AlbumRepository
import com.beesharp.backend.repository.ReviewRepository
import com.beesharp.backend.*

fun Application.configureRouting() {

    val userRepo = UserRepository()
    val albumRepo = AlbumRepository()
    val reviewRepo = ReviewRepository()

    routing {
        /*get("/{username}") {
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
                    albumList.add(resultSet.getInt("album_id1"))
                    albumList.add(resultSet.getInt("album_id2"))
                    albumList.add(resultSet.getInt("album_id3"))
                    albumList.add(resultSet.getInt("album_id4"))
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
                    "username" to user.username,
                    "email" to user.email,
                    "albums" to albums,
                    "following" to following
                )
            )
        }*/

        route("/users") {
                intercept(ApplicationCallPipeline.Call) {
        println("Requisição recebida em /users: método ${call.request.httpMethod.value}, path: ${call.request.uri}")
        proceed()
    }
            get {
                call.respond(userRepo.getAllUsers())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

                val user = userRepo.getUserById(id)
                if (user != null) call.respond(user)
                else call.respond(HttpStatusCode.NotFound)
            }

            post {
                val body = call.receive<Map<String, String>>()
                val username = body["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val email = body["email"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val passwordHash = body["passwordHash"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                val newId = userRepo.addUser(username, passwordHash, email)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
        }

        route("/albums") {
            get {
                call.respond(albumRepo.getAllAlbums())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val album = id?.let { albumRepo.getAlbumById(it) }
                if (album != null) call.respond(album)
                else call.respond(HttpStatusCode.NotFound)
            }

            post {
                val body = call.receive<Map<String, String>>()
                val title = body["title"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val artist = body["artist"]

                val newId = albumRepo.addAlbum(title, artist)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
        }

        route("/reviews") {
            get {
                call.respond(reviewRepo.getAllReviews())
            }

            get("/album/{albumId}") {
                val albumId = call.parameters["albumId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid album ID")
                val reviews = reviewRepo.getReviewsByAlbum(albumId)
                call.respond(reviews)
            }

            post {
                val body = call.receive<Map<String, String>>()
                val userId = body["userId"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val albumId = body["albumId"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val content = body["content"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val rating = body["rating"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)

                val newId = reviewRepo.addReview(userId, albumId, content, rating)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
        }

    }
}
