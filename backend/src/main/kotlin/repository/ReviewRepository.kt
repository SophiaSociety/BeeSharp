package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.ResultRow

import java.time.LocalDate
import java.time.LocalDateTime

import com.beesharp.backend.dto.*
import com.beesharp.backend.models.*

import org.jetbrains.exposed.dao.id.EntityID

class ReviewRepository {

    fun getAllReviews(): List<ReviewFullDTO> = transaction {
        (Reviews innerJoin Users).selectAll().map {
            ReviewFullDTO(
                id = it[Reviews.id],
                userId = it[Reviews.userId],
                username = it[Users.username],
                albumId = it[Reviews.albumId],
                content = it[Reviews.content],
                rating = it[Reviews.rating],
                createdAt = it[Reviews.createdAt],
                likesCount = getLikesCount(it[Reviews.id]) 
            )
        }
    }

    fun getReviewsByAlbum(albumId: Int): List<ReviewFullDTO> = transaction {
        (Reviews innerJoin Users)
            .select { Reviews.albumId eq albumId }
            .map {
                ReviewFullDTO(
                    id = it[Reviews.id],
                    userId = it[Reviews.userId],
                    username = it[Users.username],
                    albumId = it[Reviews.albumId],
                    content = it[Reviews.content],
                    rating = it[Reviews.rating],
                    createdAt = it[Reviews.createdAt],
                    likesCount = getLikesCount(it[Reviews.id]) 
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


    fun likeReview(reviewId: Int, userId: Int): Boolean = transaction {
        ReviewLikes.insertIgnore {
            it[review] = reviewId
            it[ReviewLikes.user] = userId
        }.insertedCount > 0
    }

    fun getLikesCount(reviewId: Int): Int = transaction {
        ReviewLikes.select { ReviewLikes.review eq reviewId}.count().toInt()
    }


    fun getFullReview(reviewId: Int): ReviewFullDTO? = transaction {
        val row = (Reviews innerJoin Users)
            .select { Reviews.id eq reviewId }
            .singleOrNull() ?: return@transaction null

        ReviewFullDTO(
            id = row[Reviews.id],
            userId = row[Reviews.userId],
            username = row[Users.username],
            albumId = row[Reviews.albumId],
            content = row[Reviews.content],
            rating = row[Reviews.rating],
            createdAt = row[Reviews.createdAt],
            likesCount = getLikesCount(row[Reviews.id])
        )
    }

}