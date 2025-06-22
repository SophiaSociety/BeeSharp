package com.beesharp.backend

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import java.io.File
import io.ktor.client.call.* // necessário para usar readBytes()

class ImageRoutesTest {

    @Test
    fun testUploadUserImage() = testApplication {
        application { module() }

        val fileBytes = File("src/test/resources/cute.jpeg").readBytes()

        val response = client.post("/upload/user-photo/2") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", fileBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"test_image.jpg\"")
                        })
                    }
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("uploaded"))
    }

    @Test
    fun testGetUserImage() = runTest {
        testApplication {
            application { module() }

            val response = client.get("/user-photo/2")

            if (response.status == HttpStatusCode.OK) {
                assertEquals(ContentType.Image.JPEG, response.contentType())
                val imageBytes = response.body<ByteArray>()
                assertTrue(imageBytes.isNotEmpty())
            } else {
                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }
    }

    @Test
    fun testUploadWithoutFileShouldFail() = testApplication {
        application { module() }

        val response = client.post("/upload/user-photo/3") {
            setBody(
                MultiPartFormDataContent(formData {}) // não envia arquivo
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("No image file received"))
    }

    @Test
    fun testGetNonexistentUserImage() = testApplication {
        application { module() }

        val response = client.get("/user-photo/999") // Assumindo que o id 999 não tem imagem

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}