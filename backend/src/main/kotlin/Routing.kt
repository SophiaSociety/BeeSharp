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

fun Application.configureRouting() {

    val userRepo = UserRepository()
    val albumRepo = AlbumRepository()
    val reviewRepo = ReviewRepository()

    routing {
        get("/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, mapOf("error" to "Username not provided")
            )

            val user = userRepo.getUserByUsername(username)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

            val albums = userRepo.getFavoriteAlbums(user.id)
            val following = userRepo.getFollowing(user.id)

            call.respond(
                mapOf(
                    "username" to user.username,
                    "email" to user.email,
                    "albums" to albums,
                    "following" to following
                )
            )
        }

        route("/users") {
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
