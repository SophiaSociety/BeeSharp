package com.beesharp.backend.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

// tabela do banco
object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 255)
    val passwordHash = varchar("password_hash", 255)
    val email = varchar("email", 255)
    val profileImage = blob("profile_image").nullable()
    val description = varchar("description", 1000).default("") 
    override val primaryKey = PrimaryKey(id)
}

// classe de dados
@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val profileImage: String? = null,
    val description: String
)