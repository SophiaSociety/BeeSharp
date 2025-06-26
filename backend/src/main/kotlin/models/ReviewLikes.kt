package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.dao.id.EntityID

object ReviewLikes : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val review = integer("review_id").references(Reviews.id)
    val user = reference("user_id", Users.id)

    init {
        index(true, review, user) // garante que um user curta só uma vez a mesma review
    }
}
