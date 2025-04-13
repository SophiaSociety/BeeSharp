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
        val myRealm = "MyRealm"
        val dbConnection = this@configureSecurity.connectToPostgres(embedded = false) // Conexão com o banco de dados

        digest("myDigestAuth") {
            digestProvider { userName, realm ->
                // Consultar o banco de dados para obter o valor de ha1 do usuário
                dbConnection.prepareStatement(
                    "SELECT ha1 FROM auth_users WHERE username = ?"
                ).use { statement ->
                    statement.setString(1, userName)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        return@digestProvider resultSet.getString("ha1")?.toByteArray(Charsets.UTF_8)
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


            if (password != repeatPassword) {
                call.respondText("Passwords do not match", status = HttpStatusCode.BadRequest)
                return@post
            }

            val realm = "MyRealm"
            val ha1 = "$username:$realm:$password".md5() // Calcula o valor de ha1


            // Inserir o novo usuário no banco de dados
            val dbConnection = connectToPostgres(embedded = false)            

            try {
                dbConnection.prepareStatement(
                    "INSERT INTO auth_users (username, ha1) VALUES (?, ?)"
                ).use { statement ->
                    statement.setString(1, username)
                    statement.setString(2, ha1)
                    statement.executeUpdate()
                }
            } catch (e: Exception) {
                log.error("Error inserting user into database", e)
                call.respondText("Internal Server Error", status = HttpStatusCode.InternalServerError)
                return@post
            }

            call.respondText("User $username signed up successfully", status = HttpStatusCode.Created)
        }

        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"] ?: ""
            val password = params["password"] ?: ""

            if (username.isBlank() || password.isBlank()) {
                call.respondText("Username and password must not be empty", status = HttpStatusCode.BadRequest)
                return@post
            }

            val realm = "MyRealm"
            val inputHa1 = "$username:$realm:$password".md5() // Calcula o ha1 com base na entrada do usuário
            val dbConnection = connectToPostgres(embedded = false)

            // Verificar as credenciais no banco de dados
            dbConnection.prepareStatement(
                "SELECT ha1 FROM auth_users WHERE username = ?"
            ).use { statement ->
                statement.setString(1, username)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val storedHa1 = resultSet.getString("ha1")

                    if (storedHa1 == inputHa1) {
                        call.respondText("Login successful", status = HttpStatusCode.OK)
                    } else {
                        call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
                    }
                } else {
                    call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
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
        val url = "jdbc:postgresql://localhost:5432/ktor"
        val user = "ktor_user" // Substitua por seu usuário do PostgreSQL
        val password = "senha_ktor" // Substitua pela sua senha do PostgreSQL

        log.info("Connecting to postgres database at $url")
        return DriverManager.getConnection(url, user, password)
    }
}