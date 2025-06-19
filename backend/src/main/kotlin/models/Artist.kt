package com.beesharp.backend.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

// tabela do banco
object Artists : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

// classe de dados
@Serializable
data class Artist(
    val id: Int,
    val name: String
)