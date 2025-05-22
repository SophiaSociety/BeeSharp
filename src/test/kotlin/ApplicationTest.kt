package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.call.*
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import java.sql.Connection

class ApplicationTest {
    @Test
    fun testSignupSuccess() = testApplication {
        application {
            configureSecurity()
        }

        val response = client.post("/signup") {
            setBody(
                listOf(
                    "username" to "testuser2",
                    "password" to "password1232",
                    "repeat-password" to "password1232",
                    "email" to "testuser@example.com2"
                ).formUrlEncode()
            )
            contentType(ContentType.Application.FormUrlEncoded)
        }

//        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("User testuser2 signed up successfully", response.bodyAsText())
    }

/*    @Test
    fun testSignupPasswordMismatch() = testApplication {
        application {
            configureSecurity()
        }

        val response = client.post("/signup") {
            setBody(
                listOf(
                    "username" to "testuser",
                    "password" to "password123",
                    "repeat-password" to "password456",
                    "email" to "testuser@example.com"
                ).formUrlEncode()
            )
            contentType(ContentType.Application.FormUrlEncoded)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Passwords do not match", response.bodyAsText())
    }

     @Test
    fun testLoginSuccess() = testApplication {
        application {
            configureSecurity()
        }

        // Simulate a user already existing in the database
        lateinit var dbConnection: java.sql.Connection
        application {
            dbConnection = connectToPostgres(embedded = false)
        }

        dbConnection.prepareStatement(
            "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)"
        ).use { statement ->
            statement.setString(1, "testuser")
            statement.setString(2, "testuser:MyRealm:password123".md5())
            statement.setString(3, "testuser@example.com")
            statement.executeUpdate()
        }

        val response = client.post("/login") {
            setBody(
                listOf(
                    "username" to "testuser",
                    "password" to "password123"
                ).formUrlEncode()
            )
            contentType(ContentType.Application.FormUrlEncoded)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Login successful", response.bodyAsText())
    } 

    @Test
    fun testLoginFailure() = testApplication {
        application {
            configureSecurity()
        }

        val response = client.post("/login") {
            setBody(
                listOf(
                    "username" to "nonexistentuser",
                    "password" to "wrongpassword"
                ).formUrlEncode()
            )
            contentType(ContentType.Application.FormUrlEncoded)
        }

        assertEquals(HttpStatusCode.Found, response.status) // Redirect to login page
        assertEquals("/static/login.html?error=Invalid%20username%20or%20password", response.headers["Location"])
    }  */
}
