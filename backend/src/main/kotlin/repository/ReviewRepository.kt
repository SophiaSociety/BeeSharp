package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.beesharp.backend.models.Review
import com.beesharp.backend.models.Reviews

class ReviewRepository {

    fun getAllReviews(): List<Review> = transaction {
        Reviews.selectAll().map {
            Review(
                id = it[Reviews.id],
                userId = it[Reviews.userId],
                albumId = it[Reviews.albumId],
                content = it[Reviews.content],
                rating = it[Reviews.rating],
                createdAt = it[Reviews.createdAt]
            )
        }
    }

    fun getReviewsByAlbum(albumId: Int): List<Review> = transaction {
        Reviews.select { Reviews.albumId eq albumId }.map {
            Review(
                id = it[Reviews.id],
                userId = it[Reviews.userId],
                albumId = it[Reviews.albumId],
                content = it[Reviews.content],
                rating = it[Reviews.rating],
                createdAt = it[Reviews.createdAt]
            )
        }
    }

    fun addReview(userId: Int, albumId: Int, content: String, rating: Int): Int = transaction {
        Reviews.insert {
            it[Reviews.userId] = userId
            it[Reviews.albumId] = albumId
            it[Reviews.content] = content
            it[Reviews.rating] = rating
            it[Reviews.createdAt] = LocalDateTime.now()
        } get Reviews.id
    }

    fun deleteReview(id: Int): Boolean = transaction {
        Reviews.deleteWhere { Reviews.id eq id } > 0
    }
}