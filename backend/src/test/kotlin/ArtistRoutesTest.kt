package com.beesharp.backend

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.beesharp.backend.module
import kotlinx.coroutines.runBlocking

class ArtistRoutesTest {

    @Test
    fun testGetAlbumById() = testApplication {
        application { module() }

        val response = client.get("/albums/1")

        println("Status: ${response.status}")
        println("Body: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Pink Floyd")) 
    }

    @Test
    fun testGetAlbumsByInvalidArtistIdReturns400() = testApplication {
        application { module() }

        val response = client.get("/artists/invalid/albums")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testGetAlbumsByNonExistentArtistIdReturns404() = testApplication {
        application { module() }

        val response = client.get("/artists/9999/albums")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}