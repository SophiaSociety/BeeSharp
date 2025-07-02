package com.beesharp.backend.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Artists : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val descricao = varchar("descricao", 1000)
    val image = varchar("image", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Artist(
    val id: Int,
    val name: String,
    val descricao: String,
    val image: String
)