package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table

object Albums : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val artist = varchar("artist", 255).nullable()
    val reviewsCount = integer("reviews_count").default(0)
    val averageRating = decimal("average_rating", 3, 2).default(0.00.toBigDecimal())
    override val primaryKey = PrimaryKey(id)
}

data class Album(
    val id: Int,
    val title: String,
    val artist: String?,
    val reviewsCount: Int,
    val averageRating: Double
)