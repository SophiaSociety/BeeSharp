package com.beesharp.backend

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.beesharp.backend.module

class UserDescriptionRoutesMockTest {

    @Test
    fun testGetDescriptionForExistingUser() = testApplication {
        application { module() }

        val response = client.get("/users/1/description")

        println("Status: ${response.status}")
        println("Body: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Curadora musical"))
    }

    @Test
    fun testGetDescriptionForNonexistentUser() = testApplication {
        application { module() }

        val response = client.get("/users/9999/description")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testGetDescriptionWithInvalidId() = testApplication {
        application { module() }

        val response = client.get("/users/abc/description")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}