package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable

object ListeningHistory : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val albumId = integer("album_id").references(Albums.id)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class ListeningHistoryEntry(
    val id: Int,
    val userId: Int,
    val albumId: Int
)