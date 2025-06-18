package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable


object Albums : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val artist = varchar("artist", 255).nullable()
    val reviewsCount = integer("reviews_count").default(0)
    val averageRating = decimal("average_rating", 3, 2).default(0.00.toBigDecimal())
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Album(
    val id: Int,
    val title: String,
    val artist: String?,
    val reviewsCount: Int,
    val averageRating: Double
)