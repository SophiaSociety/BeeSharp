package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table

// tabela do banco
object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 255)
    val passwordHash = varchar("password_hash", 255)
    val email = varchar("email", 255)
    override val primaryKey = PrimaryKey(id)
}

// classe de dados
data class User(
    val id: Int,
    val username: String,
    val email: String
)