package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.beesharp.backend.models.User
import com.beesharp.backend.models.Users
import com.beesharp.backend.models.AlbumFavorites
import com.beesharp.backend.models.UserFollows

class UserRepository {

    fun getAllUsers(): List<User> = transaction {
        Users.selectAll().map {
            User(
                id = it[Users.id],
                username = it[Users.username],
                email = it[Users.email],
                passwordHash = ""
            )
        }
    }

    fun getUserById(id: Int): User? = transaction {
        Users.select { Users.id eq id }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = ""
                )
            }
            .singleOrNull()
    }

    fun addUser(username: String, passwordHash: String, email: String): Int = transaction {
        val id = Users.insert {
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
            it[Users.email] = email
        } get Users.id
        println("Usuário cadastrado com sucesso: $username (id=$id, email=$email)")
        id
    }

    fun deleteUser(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.id eq id } > 0
    }

    fun updateEmail(id: Int, newEmail: String): Boolean = transaction {
        Users.update({ Users.id eq id }) {
            it[email] = newEmail
        } > 0
    }

    fun existsByUsernameOrEmail(username: String, email: String): Boolean = transaction {
        Users.select { (Users.username eq username) or (Users.email eq email) }.count() > 0
    }

    fun getUserByUsername(username: String): User? = transaction {
        Users.select { Users.username eq username }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash]
                )
            }
            .singleOrNull()
    }

    fun searchUsersByUsername(partialName: String): List<User> = transaction {
        Users.select { Users.username.lowerCase() like "%${partialName.lowercase()}%" }
            .map { User(
                        id = it[Users.id], 
                        username = it[Users.username],
                        email = it[Users.email],
                        passwordHash = ""
                    )
                }
    }

    fun getFavoriteAlbums(userId: Int): List<Int> = transaction {
        AlbumFavorites.select { AlbumFavorites.userId eq userId }
            .map {
                listOfNotNull(
                    it[AlbumFavorites.albumId1],
                    it[AlbumFavorites.albumId2],
                    it[AlbumFavorites.albumId3],
                    it[AlbumFavorites.albumId4]
                )
            }.flatten()
    }

    fun getFollowing(userId: Int): List<String> = transaction {
        UserFollows
            .join(Users, JoinType.INNER, UserFollows.userId, Users.id)
            .slice(Users.username)
            .select { UserFollows.followerId eq userId }
            .map { it[Users.username] }
    }
    fun followUser(followerId: Int, userIdToFollow: Int): Boolean = transaction {
        // Verifica se já existe o follow
        val exists = UserFollows.select {
            (UserFollows.followerId eq followerId) and (UserFollows.userId eq userIdToFollow)
        }.count() > 0

        if (exists) return@transaction false

        UserFollows.insert {
            it[UserFollows.followerId] = followerId
            it[UserFollows.userId] = userIdToFollow
        }
        true
    }
}
