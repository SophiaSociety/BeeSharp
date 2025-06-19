package com.beesharp.backend

import com.beesharp.backend.module

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


// testa as rotas relacionadas a usuários (/users, /users/{id}) com base nos dados do mock.sql
class UserRoutesTest {
    @Test
    // verifica se a rota `/users` está acessível e retorna status 200
    fun testGetUsersReturns200() = testApplication {
        application {
            module()
        }

        val response = client.get("/users") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test

    // teste que verifica a presença do usuário com id = 1 (alice)
    fun testGetUserById() = testApplication {
        application {
            module()
        }

        val response = client.get("/users/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}