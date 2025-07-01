package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable


object Albums : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val artist = varchar("artist", 255).nullable()
    val year = varchar("year", 10).nullable() // Novo campo
    val genre = varchar("genre", 100).nullable() // Novo campo
    val duration = varchar("duration", 10).nullable() // Novo campo
    val averageRating = decimal("average_rating", 3, 2).default(0.00.toBigDecimal())
    val reviewsCount = integer("reviews_count").default(0)
    val totalRatings = integer("total_ratings").default(0) // Novo campo
    val image = varchar("image", 255).nullable() // Novo campo
    val description = text("description").nullable() // Novo campo
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Album(
    val id: Int,
    val title: String,
    val artist: String?,
    val year: String? = null,
    val genre: String? = null,
    val duration: String? = null,
    val averageRating: Double,
    val reviewsCount: Int,
    val totalRatings: Int = 0,
    val image: String? = null,
    val description: String? = null
)