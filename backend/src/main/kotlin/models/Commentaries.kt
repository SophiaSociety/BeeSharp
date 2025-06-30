package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.dao.id.EntityID

object Commentaries : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val review = reference("review_id", Reviews.id)
    val user = reference("user_id", Users.id)
    val commentary = text("commentary")
    val creationDate = date("creation_date").default(LocalDate.now())
    val modifiedDate = date("modified_date").default(LocalDate.now())
}
