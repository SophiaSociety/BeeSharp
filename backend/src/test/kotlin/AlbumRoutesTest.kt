package com.beesharp.backend

import com.beesharp.backend.module

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// testes que validam a listagem e detalhes dos álbuns disponíveis via /albums e /albums/{id}
class AlbumRoutesTest {
    @Test
    fun testGetAlbumsReturns200() = testApplication {
        application { module() }
        val response = client.get("/albums")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetAlbumById() = testApplication {
        application { module() }
        val response = client.get("/albums/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("The Wall"))
    }
}