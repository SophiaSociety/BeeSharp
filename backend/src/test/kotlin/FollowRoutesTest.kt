package com.beesharp.backend

import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.call.*
import io.ktor.client.HttpClient
import com.beesharp.backend.module

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class FollowRoutesDataTest {

    // Auxiliar para login e retorno do token
    private suspend fun loginAndGetToken(client: HttpClient, username: String, password: String): String {
        val response = client.post("/login") {
            setBody(listOf("username" to username, "password" to password).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<Map<String, String>>()
        return body["token"] ?: error("Token não encontrado")
    }

    @Test
    fun testReadFollowersAndFollowingFromSeed() = testApplication {
        application { module() }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val token = loginAndGetToken(client, "alice", "hash1")

        val responseFollowers = client.get("/users/1/followers") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, responseFollowers.status)
        val followers = responseFollowers.body<List<Map<String, Any>>>()
        val followerUsernames = followers.map { it["username"] }
        assertTrue("bob" in followerUsernames)

        val responseFollowing = client.get("/users/1/following") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        println("Following status: ${responseFollowing.status}")
        println("Following response body:\n${responseFollowing.bodyAsText()}") 

        assertEquals(HttpStatusCode.OK, responseFollowing.status)
        val following = responseFollowing.body<List<Map<String, Any>>>()
        val followingUsernames = following.map { it["username"] }
        assertTrue("judy" in followingUsernames)
    }


    @Test
        fun testGetFollowersAndFollowingUsingSeedData() = testApplication {
            application { module() }

            val client = createClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }

            // Autentica como "alice" (usuário com ID 1 no seed) e pega o token
            val token = loginAndGetToken(client, "alice", "hash1") 

            val expectedFollowersOf1 = listOf("bob")     // ID 2
            val expectedFollowingOf1 = listOf("judy")    // ID 10

            // Requisição: pegar seguidores de alice (ID 1)
            val followersResponse = client.get("/users/1/followers") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            println("Followers status: ${followersResponse.status}")
            println("Followers response body:\n${followersResponse.bodyAsText()}") 

            assertEquals(HttpStatusCode.OK, followersResponse.status)
            val followersBody = followersResponse.body<List<Map<String, Any>>>()
            val followerUsernames = followersBody.map { it["username"] }
            assertTrue(expectedFollowersOf1.all { it in followerUsernames })

            // Requisição: pegar quem alice está seguindo
            val followingResponse = client.get("/users/1/following") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            assertEquals(HttpStatusCode.OK, followingResponse.status)
            val followingBody = followingResponse.body<List<Map<String, Any>>>()
            val followingUsernames = followingBody.map { it["username"] }
            assertTrue(expectedFollowingOf1.all { it in followingUsernames })
        }
}
