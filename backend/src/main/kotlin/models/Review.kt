package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Reviews : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references Users.id
    val albumId = integer("album_id") references Albums.id
    val content = text("content")
    val rating = integer("rating")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

data class Review(
    val id: Int,
    val userId: Int,
    val albumId: Int,
    val content: String,
    val rating: Int,
    val createdAt: LocalDateTime
)