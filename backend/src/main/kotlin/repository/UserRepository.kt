package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.beesharp.backend.models.User
import com.beesharp.backend.models.Users
import com.beesharp.backend.models.AlbumFavorites
import com.beesharp.backend.models.UserFollows
import com.beesharp.backend.models.ListeningHistory
import com.beesharp.backend.models.Album
import com.beesharp.backend.models.Reviews
import com.beesharp.backend.repository.AlbumRepository
import com.beesharp.backend.repository.ReviewRepository

class UserRepository {

    val albumRepo = AlbumRepository()

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
        Users.insert {
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
            it[Users.email] = email
        } get Users.id
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

    fun recommendAlbumsForUser(userId: Int, limit: Int = 20, albumRepo: AlbumRepository): List<Album> = transaction {
        // 1. Álbuns já ouvidos pelo usuário
        val listenedAlbumIds = ListeningHistory
            .slice(ListeningHistory.albumId)
            .select { ListeningHistory.userId eq userId }
            .map { it[ListeningHistory.albumId] }
            .toSet()

        // 2. Pessoas que o usuário segue
        val followingUserIds = UserFollows
            .slice(UserFollows.userId)
            .select { UserFollows.followerId eq userId }
            .map { it[UserFollows.userId] }
            .toSet()

        // 3. Álbuns bem avaliados por amigos (seguindo)
        val friendRecommendedAlbumIds = Reviews
            .slice(Reviews.albumId)
            .select { (Reviews.userId inList followingUserIds) and (Reviews.rating greaterEq 8) }
            .map { it[Reviews.albumId] }
            .toSet()

        // 4. Álbuns bem avaliados em geral
        val topRatedAlbums = albumRepo.getTopRatedAlbums(100).map { it.id }

        // 5. Combine: priorize recomendações de amigos, depois top-rated geral, excluindo os já ouvidos
        val candidateAlbumIds = (friendRecommendedAlbumIds + topRatedAlbums)
            .filter { it !in listenedAlbumIds }
            .distinct()
            .take(limit)

        // 6. Busque os álbuns completos usando o AlbumRepository
        candidateAlbumIds.mapNotNull { albumRepo.getAlbumById(it) }
    }
}
