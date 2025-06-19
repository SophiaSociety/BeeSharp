package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table

object UserFollows : Table() {
    val userId = integer("user_id").references(Users.id)
    val followerId = integer("follower_id").references(Users.id)

    override val primaryKey = PrimaryKey(userId, followerId)
}

data class UserFollow(
    val userId: Int,
    val followerId: Int
)