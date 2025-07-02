package com.beesharp.backend.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDate
import java.time.LocalDateTime

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
    val likesCount: Int
)
