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
import com.beesharp.backend.dto.ReviewFullDTO

import java.util.Base64

class UserRepository {

    val albumRepo = AlbumRepository()

    fun getAllUsers(): List<User> = transaction {
        Users.selectAll().map {
            User(
                id = it[Users.id],
                username = it[Users.username],
                email = it[Users.email],
                passwordHash = "",
                description = it[Users.description],
                profileImage = it[Users.profileImage]?.bytes?.let { bytes ->
                    Base64.getEncoder().encodeToString(bytes)
                }
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
                    passwordHash = "",
                    description = it[Users.description],
                    profileImage = it[Users.profileImage]?.bytes?.let { bytes ->
                        Base64.getEncoder().encodeToString(bytes)
                    }
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
                    passwordHash = it[Users.passwordHash],
                    description = it[Users.description],
                    profileImage = it[Users.profileImage]?.bytes?.let { bytes ->
                        Base64.getEncoder().encodeToString(bytes)
                    }
                )
            }
            .singleOrNull()
    }

    fun searchUsersByUsername(partialName: String): List<User> = transaction {
        Users.select { Users.username.lowerCase() like "%${partialName.lowercase()}%" }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = "",
                    description = it[Users.description],
                    profileImage = it[Users.profileImage]?.bytes?.let { bytes ->
                        Base64.getEncoder().encodeToString(bytes)
                    }
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

    fun favoriteAlbum(userId: Int, albumId: Int) = transaction {
        // Busca favoritos atuais
        val fav = AlbumFavorites.select { AlbumFavorites.userId eq userId }.singleOrNull()
        val currentFavs = listOfNotNull(
            fav?.getOrNull(AlbumFavorites.albumId1),
            fav?.getOrNull(AlbumFavorites.albumId2),
            fav?.getOrNull(AlbumFavorites.albumId3),
            fav?.getOrNull(AlbumFavorites.albumId4)
        )

        // Se já é favorito, remove da lista para evitar duplicidade
        val newFavs = listOf(albumId) + currentFavs.filter { it != albumId }

        // Mantém só os 4 primeiros
        val paddedFavs = (newFavs + listOf(null, null, null, null)).take(4)

        // Atualiza ou insere
        if (fav == null) {
            AlbumFavorites.insert {
                it[AlbumFavorites.userId] = userId
                it[AlbumFavorites.albumId1] = paddedFavs[0]
                it[AlbumFavorites.albumId2] = paddedFavs[1]
                it[AlbumFavorites.albumId3] = paddedFavs[2]
                it[AlbumFavorites.albumId4] = paddedFavs[3]
            }
        } else {
            AlbumFavorites.update({ AlbumFavorites.userId eq userId }) {
                it[AlbumFavorites.albumId1] = paddedFavs[0]
                it[AlbumFavorites.albumId2] = paddedFavs[1]
                it[AlbumFavorites.albumId3] = paddedFavs[2]
                it[AlbumFavorites.albumId4] = paddedFavs[3]
            }
        }
    }

    fun getRecentFriendReviews(userId: Int, limit: Int = 20): List<ReviewFullDTO> = transaction {
        // Descobre os IDs dos amigos (usuários seguidos)
        val followingUserIds = UserFollows
            .slice(UserFollows.userId)
            .select { UserFollows.followerId eq userId }
            .map { it[UserFollows.userId] }

        if (followingUserIds.isEmpty()) return@transaction emptyList()

        // Busca reviews recentes desses amigos
        (Reviews innerJoin Users)
            .select { Reviews.userId inList followingUserIds }
            .orderBy(Reviews.createdAt, SortOrder.DESC)
            .limit(limit)
            .map {
                ReviewFullDTO(
                    id = it[Reviews.id],
                    userId = it[Reviews.userId],
                    username = it[Users.username],
                    albumId = it[Reviews.albumId],
                    content = it[Reviews.content],
                    rating = it[Reviews.rating],
                    createdAt = it[Reviews.createdAt],
                    likesCount = 0 // ou use seu método getLikesCount se quiser
                )
            }
    }
}
