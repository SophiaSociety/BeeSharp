package com.beesharp.backend

import com.beesharp.backend.module

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// teste a presença de um username e retorna o perfil do usuário com email, favoritos e quem ele segue
class ProfileRouteTest {

    @Test
    fun testGetProfileByUsername() = testApplication {
        application { module() }

        val response = client.get("/alice")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        assertTrue(body.contains("email"))
        assertTrue(body.contains("albums"))
        assertTrue(body.contains("following"))
    }

    @Test
    // garante que a aplicação responde corretamente (com 404) quando um usuário inexistente é solicitado
    fun testGetProfileNotFound() = testApplication {
        application { module() }

        val response = client.get("/nonexistentuser")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
