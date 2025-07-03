package com.beesharp.backend

import com.beesharp.backend.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ArtistByAlbumRoutesTest {
    @Test
    fun testGetArtistsByAlbumId_returnsListOfArtists() = testApplication {
        application { module() }

        // Substitua o ID pelo que você sabe que existe no seu mock
        val response = client.get("/albums/1/artists")

        println("Status: ${response.status}")
        println("Body: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("id"))
        assertTrue(response.bodyAsText().contains("name"))
    }

    @Test
    fun testGetArtistsByAlbumId_invalidId_returnsBadRequest() = testApplication {
        application { module() }

        val response = client.get("/albums/abc/artists")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testGetArtistsByAlbumId_notFound_returnsNotFound() = testApplication {
        application { module() }

        val response = client.get("/albums/9999/artists")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}