package com.beesharp.backend

import com.beesharp.backend.module

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// testa o endpoint de reviews, especialmente reviews associadas a álbuns existentes no banco
class ReviewRoutesTest {
    @Test
    fun testGetReviewsReturns200() = testApplication {
        application { module() }
        val response = client.get("/reviews")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetReviewsByAlbum() = testApplication {
        application { module() }
        val response = client.get("/reviews/album/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Incrível álbum"))
    }
}