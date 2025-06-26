package com.beesharp.backend.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class ReviewCommentDTO(
    val id: Int,
    val reviewId: Int,
    val userId: Int,
    val commentary: String,
    @Contextual val creationDate: LocalDate,
    @Contextual val modifiedDate: LocalDate,
    val username: String
)

@Serializable
data class ReviewLikeDTO(
    val reviewId: Int,
    val userId: Int,
    @Contextual val createdAt: LocalDateTime
)

@Serializable
data class ReviewFullDTO(
    val id: Int,
    val userId: Int,
    val username: String,
    val albumId: Int,
    val content: String,
    val rating: Int,
    @Contextual val createdAt: LocalDateTime,
    val comments: List<ReviewCommentDTO>,
    val likesCount: Int
)
