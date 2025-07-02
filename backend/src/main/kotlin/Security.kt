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

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.beesharp.backend.models.Users
import com.beesharp.backend.models.UserFollows
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

private const val jwtIssuer = "beesharp"
private const val jwtAudience = "beesharp-users"
private const val jwtRealm = "beesharp"
private const val jwtSecret = "super-secret-key" // Troque por algo seguro em produção


@kotlinx.serialization.Serializable
data class SignupResponse(
    val message: String,
    val id: Int
)

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
            try {
                val params = call.receiveParameters()
                val username = params["username"] ?: ""
                val password = params["password"] ?: ""
                val repeatPassword = params["repeat-password"] ?: ""
                val email = params["email"] ?: ""
                println("Dados recebidos: $username, $email, $password, $repeatPassword")

                if (password != repeatPassword) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Passwords do not match"))
                    return@post
                }

                val userRepo = UserRepository()
                if (userRepo.existsByUsernameOrEmail(username, email)) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Username or email already exists"))
                    return@post
                }

                val userId = userRepo.addUser(username, password.md5(), email)
                call.respond(HttpStatusCode.Created, SignupResponse("User $username signed up successfully", userId))
            } catch (e: Exception) {
                e.printStackTrace() // <-- Adicione isto para ver o erro real no console
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Erro internaoo no servidor"))
            }
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

            val userRepo = UserRepository()
            val user = userRepo.getUserByUsername(username)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                return@post
            }

            if (user.passwordHash == password.md5()) {
                // Gera o token JWT
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .withClaim("username", username)
                    .withClaim("userId", user.id)
                    .withClaim("exp", (System.currentTimeMillis() / 1000) + (24 * 60 * 60)) // 24h in seconds
                    .sign(Algorithm.HMAC256(jwtSecret))
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
            }
        }

        // Rotas protegidas por JWT
        authenticate("auth-jwt") {
            get("/protected/route/jwt") {
                val principal = call.principal<JWTPrincipal>()!!
                val username = principal.payload.getClaim("username").asString()
                call.respondText("Hello $username, you are authenticated via JWT!")
            }

            // follow
            post("/follow/{userIdToFollow}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userIdAgent = principal.payload.getClaim("userId").asInt() // O ID do usuário autenticado deve estar no JWT!
                val userIdToFollow = call.parameters["userIdToFollow"]?.toIntOrNull()

                if (userIdToFollow == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "User ID to follow not provided or invalid"))
                    return@post
                }

                if (userIdAgent == userIdToFollow) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You cannot follow yourself"))
                    return@post
                }

                val userRepo = UserRepository()
                val followed = userRepo.getUserById(userIdToFollow)
                if (followed == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User to follow not found"))
                    return@post
                }

                val success = userRepo.followUser(userIdAgent, userIdToFollow)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Now following user with id $userIdToFollow"))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Already following or error occurred"))
                }
            }
            // quem segue o usuário
            get("/users/{id}/followers") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@get
                }

                try {
                    val followers = transaction {
                        UserFollows
                            .innerJoin(Users, { UserFollows.followerId }, { Users.id })
                            .select { UserFollows.userId eq userId }
                            .map {
                                mapOf(
                                    "id" to it[UserFollows.followerId],
                                    "username" to it[Users.username]
                                )
                            }
                    }
                    call.respond(HttpStatusCode.OK, followers)
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Erro: ${e.message}")
                    call.application.environment.log.error("Erro ao buscar followers", e)
                    call.respond(HttpStatusCode.InternalServerError, "Erro no servidor")
                }
            }

            //quem o usuário ta seguindo
            get("/users/{id}/following") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@get
                }

                try {
                    val following = transaction {
                        UserFollows
                            .innerJoin(Users, { UserFollows.userId }, { Users.id })
                            .select { UserFollows.followerId eq userId }           
                            .map {
                                mapOf(
                                    "id" to it[UserFollows.userId],
                                    "username" to it[Users.username]
                                )
                            }
                    }
                    call.respond(HttpStatusCode.OK, following)
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Erro: ${e.message}")
                    call.application.environment.log.error("Erro ao buscar following", e)
                    call.respond(HttpStatusCode.InternalServerError, "Erro no servidor")
                }
            }

            // unfollow
            delete("/users/{id}/follow") {
                val userId = call.parameters["id"]?.toIntOrNull()
                val followerId = call.request.queryParameters["followerId"]?.toIntOrNull()

                if (userId == null || followerId == null) {
                    call.respond(HttpStatusCode.BadRequest, "IDs inválidos")
                    return@delete
                }

                transaction {
                    UserFollows.deleteWhere {
                        (UserFollows.userId eq userId) and (UserFollows.followerId eq followerId)
                    }
                }

                call.respond(HttpStatusCode.OK, "Deixou de seguir.")
            }

            route("/users/{id}/description") {
                put {
                    val userId = call.parameters["id"]?.toIntOrNull()
                    if (userId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        return@put
                    }

                    val request = call.receive<Map<String, String>>()
                    val newDescription = request["description"]

                    if (newDescription.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, "Description is required")
                        return@put
                    }

                    val updated = transaction {
                        Users.update({ Users.id eq userId }) {
                            it[description] = newDescription
                        } > 0
                    }

                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Description updated successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                }
            }


            post("/review") {
                val principal = call.principal<JWTPrincipal>()!!
                val userIdAgent = principal.payload.getClaim("userId").asInt() // O ID do usuário autenticado deve estar no JWT!

                val params = call.receiveParameters()
                val albumId = params["albumId"]?.toIntOrNull()
                val content = params["content"]
                val rating = params["rating"]?.toIntOrNull()

                if (albumId == null || content.isNullOrBlank() || rating == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid fields"))
                    return@post
                }

                val reviewRepo = ReviewRepository()
                val newId = reviewRepo.addReview(userIdAgent, albumId, content, rating)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
            delete("/review/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                
                val reviewRepo = ReviewRepository()
                val deleted = reviewRepo.deleteReview(id)
                if (deleted) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            }

            post("/review/{id}/like") {
                val reviewId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()

                val reviewRepo = ReviewRepository()
                val liked = reviewRepo.likeReview(reviewId, userId)
                call.respond(if (liked) HttpStatusCode.OK else HttpStatusCode.Conflict)
            }
            post("/favorite/{albumId}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asInt()
                val albumId = call.parameters["albumId"]?.toIntOrNull()

                if (albumId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid albumId"))
                    return@post
                }

                val userRepo = UserRepository()
                userRepo.favoriteAlbum(userId, albumId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Album $albumId favorited!"))
            }
            get("/recommendations") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asInt()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val userRepo = UserRepository()
                val albumRepo = userRepo.albumRepo // ou crie uma nova instância se preferir

                val recommendations = userRepo.recommendAlbumsForUser(userId, limit, albumRepo)
                call.respond(recommendations)
            }
            get("/friends/recent-reviews") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asInt()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val userRepo = UserRepository()
                val reviews = userRepo.getRecentFriendReviews(userId, limit)
                call.respond(reviews)
            }
        }
    }
}

// Função de hash MD5
fun String.md5(): String {
    val bytes = java.security.MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}