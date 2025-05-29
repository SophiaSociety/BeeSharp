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

fun Application.configureSecurity() {
    authentication {
        val dbConnection = this@configureSecurity.connectToPostgres(embedded = false) // Conexão com o banco de dados

        digest("myDigestAuth") {
            digestProvider { userName, _ -> // O parâmetro 'realm' não é usado
                // Consultar o banco de dados para obter o valor de password_hash do usuário
                dbConnection.prepareStatement(
                    "SELECT password_hash FROM users WHERE username = ?"
                ).use { statement ->
                    statement.setString(1, userName)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        return@digestProvider resultSet.getString("password_hash")?.toByteArray(Charsets.UTF_8)
                    }
                }
                null // Retorna null se o usuário não for encontrado
            }
        }
    }

    routing {
        authenticate("myDigestAuth") {
            get("/protected/route/digest") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }    

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

            val realm = "MyRealm"
            val passwordHash = "$username:$realm:$password".md5()

            val dbConnection = connectToPostgres(embedded = false)
            try {
                dbConnection.prepareStatement(
                    "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)"
                ).use { statement ->
                    statement.setString(1, username)
                    statement.setString(2, passwordHash)
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

            val realm = "MyRealm"
            val inputPasswordHash = "$username:$realm:$password".md5()
            val dbConnection = connectToPostgres(embedded = false)

            dbConnection.prepareStatement(
                "SELECT password_hash FROM users WHERE username = ?"
            ).use { statement ->
                statement.setString(1, username)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val storedPasswordHash = resultSet.getString("password_hash")

                    if (storedPasswordHash == inputPasswordHash) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful"))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                }
            }
        }
        
    }
}

// Função de hash MD5
fun String.md5(): String {
    val bytes = java.security.MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        // Configuração para o banco de dados PostgreSQL local
        val url = "jdbc:postgresql://localhost:5432/beesharp?user=beesharp_user&password=supersecret"
        log.info("Connecting to postgres database at $url")
        return DriverManager.getConnection(url)
    }
}