package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.beesharp.backend.models.Review
import com.beesharp.backend.models.Reviews
import com.beesharp.backend.models.Albums

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
        val reviewId = Reviews.insert {
            it[Reviews.userId] = userId
            it[Reviews.albumId] = albumId
            it[Reviews.content] = content
            it[Reviews.rating] = rating
            it[Reviews.createdAt] = LocalDateTime.now()
        } get Reviews.id

        // 2. Busca todas as notas do álbum
        val ratings = Reviews.slice(Reviews.rating)
            .select { Reviews.albumId eq albumId }
            .map { it[Reviews.rating] }

        // 3. Calcula média e quantidade
        val reviewCount = ratings.size
        val average = if (reviewCount > 0) ratings.average() else 0.0

        // 4. Atualiza o álbum
        Albums.update({ Albums.id eq albumId }) {
            it[Albums.averageRating] = average.toBigDecimal()
            it[Albums.reviewsCount] = reviewCount
        }

        reviewId
    }

    fun deleteReview(id: Int): Boolean = transaction {
        Reviews.deleteWhere { Reviews.id eq id } > 0
    }
}