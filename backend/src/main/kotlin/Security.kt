package com.beesharp.backend.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import com.beesharp.backend.*
import com.beesharp.backend.repository.UserRepository
import com.beesharp.backend.repository.ReviewRepository

private const val jwtIssuer = "beesharp"
private const val jwtAudience = "beesharp-users"
private const val jwtRealm = "beesharp"
private const val jwtSecret = "super-secret-key" // Troque por algo seguro em produção

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    routing {
        // Rotas públicas
        post("/signup") {
            val params = call.receiveParameters()
            val username = params["username"] ?: ""
            val password = params["password"] ?: ""
            val repeatPassword = params["repeat-password"] ?: ""
            val email = params["email"] ?: ""
            println("Dados recebidos: $username, $email")

            if (password != repeatPassword) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Passwords do not match"))
                return@post
            }

            val dbConnection = connectToPostgres(embedded = false)
            try {
                dbConnection.prepareStatement(
                    "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)"
                ).use { statement ->
                    statement.setString(1, username)
                    statement.setString(2, password.md5())
                    statement.setString(3, email)
                    statement.executeUpdate()
                }
            } catch (e: Exception) {
                log.error("Error inserting user into database", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal Server Error"))
                return@post
            }

            call.respond(HttpStatusCode.Created, mapOf("message" to "User $username signed up successfully"))
        }

        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"] ?: ""
            val password = params["password"] ?: ""
            println("Dados recebidos: $username")

            if (username.isBlank() || password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username and password must not be empty"))
                return@post
            }

            val dbConnection = connectToPostgres(embedded = false)
            dbConnection.prepareStatement(
                "SELECT password_hash FROM users WHERE username = ?"
            ).use { statement ->
                statement.setString(1, username)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val storedPasswordHash = resultSet.getString("password_hash")
                    if (storedPasswordHash == password.md5()) {
                        // Gera o token JWT
                        val token = JWT.create()
                            .withAudience(jwtAudience)
                            .withIssuer(jwtIssuer)
                            .withClaim("username", username)
                            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24h
                            .sign(Algorithm.HMAC256(jwtSecret))
                        call.respond(HttpStatusCode.OK, mapOf("token" to token))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                }
            }
        }

        // Rotas protegidas por JWT
        authenticate("auth-jwt") {
            get("/protected/route/jwt") {
                val principal = call.principal<JWTPrincipal>()!!
                val username = principal.payload.getClaim("username").asString()
                call.respondText("Hello $username, you are authenticated via JWT!")
            }
            post("/follow/{usernameToFollow}") {
                val principal = call.principal<JWTPrincipal>()!!
                val followerUsername = principal.payload.getClaim("username").asString()
                val usernameToFollow = call.parameters["usernameToFollow"]

                if (usernameToFollow.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username to follow not provided"))
                    return@post
                }

                val userRepo = UserRepository()
                val follower = userRepo.getUserByUsername(followerUsername)
                val followed = userRepo.getUserByUsername(usernameToFollow)

                if (follower == null || followed == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    return@post
                }

                // Evita seguir a si mesmo
                if (follower.id == followed.id) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You cannot follow yourself"))
                    return@post
                }

                // Adiciona o follow (implemente esse método no UserRepository se ainda não existir)
                val success = userRepo.followUser(follower.id, followed.id)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Now following $usernameToFollow"))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Already following or error occurred"))
                }
            }
            post("/review") {
                val principal = call.principal<JWTPrincipal>()!!
                val username = principal.payload.getClaim("username").asString()

                val userRepo = UserRepository()
                val reviewRepo = ReviewRepository()
                val user = userRepo.getUserByUsername(username)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
                    return@post
                }

                val params = call.receiveParameters()
                val albumId = params["albumId"]?.toIntOrNull()
                val content = params["content"]
                val rating = params["rating"]?.toIntOrNull()

                if (albumId == null || content.isNullOrBlank() || rating == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid fields"))
                    return@post
                }

                val newId = reviewRepo.addReview(user.id, albumId, content, rating)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
        }
    }
}

// Função de hash MD5
fun String.md5(): String {
    val bytes = java.security.MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}