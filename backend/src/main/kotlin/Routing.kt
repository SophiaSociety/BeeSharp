package com.beesharp.backend

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.util.*
import java.sql.Connection
import java.sql.DriverManager
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

import com.beesharp.backend.repository.UserRepository
import com.beesharp.backend.repository.AlbumRepository
import com.beesharp.backend.repository.ReviewRepository
import com.beesharp.backend.repository.ArtistRepository
import com.beesharp.backend.*
import com.beesharp.backend.models.Users
import com.beesharp.backend.models.Album
import com.beesharp.backend.models.ListeningHistory
import com.beesharp.backend.models.Reviews
import com.beesharp.backend.models.UserFollows
import com.beesharp.backend.models.AlbumFavorites
import com.beesharp.backend.models.ListeningHistoryEntry
import kotlinx.serialization.Serializable

import io.ktor.http.content.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import com.beesharp.backend.dto.*

@Serializable
data class UserProfileResponse(
    val id: Int, // <-- adicione este campo
    val username: String,
    val email: String,
    val albums: List<Int>,
    val following: List<String>
)

@kotlinx.serialization.Serializable
data class UserStatsResponse(
    val totalAlbums: Int,
    val totalReviews: Int,
    val following: Int,
    val followers: Int,
    val averageRating: Double,
    val albumsThisYear: Int,
    val totalFavorites: Int
)

var hotAlbumsCache: Pair<Long, List<Album>>? = null
const val HOT_ALBUMS_CACHE_TTL = 5 * 60 * 1000 // 5 minutos
var topRatedAlbumsCache: Pair<Long, List<Album>>? = null
const val TOP_RATED_ALBUMS_CACHE_TTL = 5 * 60 * 1000 // 5 minutos

fun Application.configureRouting() {

    val userRepo = UserRepository()
    val albumRepo = AlbumRepository()
    val reviewRepo = ReviewRepository()
    val artistRepo = ArtistRepository()

    routing {
        get("/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, mapOf("error" to "Username not provided")
            )

            // Buscar usuário pelo repositório
            val user = userRepo.getUserByUsername(username)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@get
            }

            // Buscar álbuns favoritos usando UserRepository
            val albums = userRepo.getFavoriteAlbums(user.id)

            // Buscar quem o usuário segue usando UserRepository
            val following = userRepo.getFollowing(user.id)   

            // Montar resposta
            val response = UserProfileResponse(
                id = user.id, // <-- adicione este campo
                username = user.username,
                email = user.email,
                albums = albums,
                following = following
            )
            println("Perfil do usuário: $response")

            call.respond(response) 
        }

        route("/users") {
            get {
                call.respond(userRepo.getAllUsers())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

                val user = userRepo.getUserById(id)
                if (user != null) call.respond(user)
                else call.respond(HttpStatusCode.NotFound)
            }

            post {
                val body = call.receive<Map<String, String>>()
                val username = body["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val email = body["email"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val passwordHash = body["passwordHash"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                val newId = userRepo.addUser(username, passwordHash, email)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
            
            route("/{id}/description") {
                get {
                    val userId = call.parameters["id"]?.toIntOrNull()
                    if (userId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        return@get
                    }

                    val description = transaction {
                        Users
                            .select { Users.id eq userId }
                            .mapNotNull { it[Users.description] }
                            .singleOrNull()
                    }

                    if (description != null) {
                        call.respond(HttpStatusCode.OK, mapOf("description" to description))
                        println("Descrição encontrada: $description")
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    }
                }
            }

            get("/{id}/stats") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                    return@get
                }

                val stats = transaction {
                    val totalAlbums = ListeningHistory.select { ListeningHistory.userId eq userId }.count().toInt()
                    val totalReviews = Reviews.select { Reviews.userId eq userId }.count().toInt()
                    val following = UserFollows.select { UserFollows.followerId eq userId }.count().toInt()
                    val followers = UserFollows.select { UserFollows.userId eq userId }.count().toInt()
                    val ratings = Reviews.slice(Reviews.rating).select { Reviews.userId eq userId }.map { it[Reviews.rating] }
                    val averageRating = if (ratings.isNotEmpty()) ratings.average() else 0.0
                    val totalFavorites = AlbumFavorites.select { AlbumFavorites.userId eq userId }
                        .map {
                            listOfNotNull(
                                it[AlbumFavorites.albumId1],
                                it[AlbumFavorites.albumId2],
                                it[AlbumFavorites.albumId3],
                                it[AlbumFavorites.albumId4]
                            )
                        }.flatten().size
                    val albumsThisYear = 0 // Ajuste se houver campo de data

                    UserStatsResponse(
                        totalAlbums = totalAlbums,
                        totalReviews = totalReviews,
                        following = following,
                        followers = followers,
                        averageRating = averageRating,
                        albumsThisYear = albumsThisYear,
                        totalFavorites = totalFavorites
                    )
                }

                call.respond(HttpStatusCode.OK, stats)
            }

            get("/{id}/favorites") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                    return@get
                }
                // Busca os álbuns favoritos do usuário (usando UserRepository)
                val favoriteIds = userRepo.getFavoriteAlbums(userId)
                val favoriteAlbums = favoriteIds.mapNotNull { albumRepo.getAlbumById(it) }

                call.respond(HttpStatusCode.OK, favoriteAlbums)
            }

            get("/{id}/listened") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                    return@get
                }
                // Busca os IDs dos álbuns escutados
                val listenedIds = transaction {
                    ListeningHistory
                        .select { ListeningHistory.userId eq userId }
                        .map { it[ListeningHistory.albumId] }
                        .distinct()
                }
                // Busca os dados completos dos álbuns
                val listenedAlbums = listenedIds.mapNotNull { albumRepo.getAlbumById(it) }
                call.respond(HttpStatusCode.OK, listenedAlbums)
            }

            get("/{id}/reviews") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                    return@get
                }
                val reviews = reviewRepo.getAllReviews().filter { it.userId == userId }
                val reviewsWithAlbum = reviews.map { review ->
                    val album = albumRepo.getAlbumById(review.albumId)
                    ReviewWithAlbumDTO(
                        id = review.id,
                        userId = review.userId,
                        username = review.username,
                        albumId = review.albumId,
                        title = album?.title ?: "",
                        artist = album?.artist ?: "",
                        year = album?.year?.toIntOrNull(), // <-- aqui está a correção
                        coverUrl = album?.image,
                        content = review.content,
                        rating = review.rating,
                        createdAt = review.createdAt.toString(),
                        likesCount = review.likesCount
                    )
                }
                call.respond(HttpStatusCode.OK, reviewsWithAlbum)
            }
        }

        route("/albums") {
            get {
                call.respond(albumRepo.getAllAlbums())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val album = id?.let { albumRepo.getAlbumById(it) }
                if (album != null) call.respond(album)
                else call.respond(HttpStatusCode.NotFound)
            }

            get("/hot") {
                val now = System.currentTimeMillis()
                val cached = hotAlbumsCache
                if (cached != null && now - cached.first < HOT_ALBUMS_CACHE_TTL) {
                    call.respond(cached.second)
                } else {
                    val hotAlbums = albumRepo.getHotAlbums()
                    hotAlbumsCache = now to hotAlbums
                    call.respond(hotAlbums)
                }
            }

            get("/top-rated") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
                val now = System.currentTimeMillis()
                val cached = topRatedAlbumsCache
                if (cached != null && now - cached.first < TOP_RATED_ALBUMS_CACHE_TTL) {
                    call.respond(cached.second.take(limit))
                } else {
                    val topRated = albumRepo.getTopRatedAlbums(limit)
                    topRatedAlbumsCache = now to topRated
                    call.respond(topRated)
                }
            }

            post {
                val body = call.receive<Map<String, String>>()
                val title = body["title"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val artist = body["artist"]

                val newId = albumRepo.addAlbum(title, artist)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }

            get("/{id}/artists") {
                val albumId = call.parameters["id"]?.toIntOrNull()
                if (albumId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid album ID")
                    return@get
                }

                val artists = artistRepo.getArtistsByAlbumId(albumId)
                if (artists.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "No artists found for album ID $albumId")
                } else {
                    call.respond(HttpStatusCode.OK, artists)
                }
            }
        }

        route("/artists") {
            get {
                call.respond(artistRepo.getAllArtists())
            }

            get("/{id}") {
                val artistId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid artist ID")
                val artist = artistRepo.getArtistById(artistId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Artist not found")
                call.respond(artist)
            }

            get("/{id}/albums") {
                val artistId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid artist ID")
                val artist = artistRepo.getArtistById(artistId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Artist not found")
                val albums = albumRepo.getAlbumsByArtist(artist.name)
                call.respond(albums)
            }
        }

        route("/reviews") {
            get {
                call.respond(reviewRepo.getAllReviews())
            }

            get("/album/{albumId}") {
                val albumId = call.parameters["albumId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid album ID")
                val reviews = reviewRepo.getReviewsByAlbum(albumId)
                call.respond(reviews)
            }

            get("/{id}/likes") {
                val reviewId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val count = reviewRepo.getLikesCount(reviewId)
                call.respond(mapOf("likes" to count))
            }

            get("/{id}/full") {
                val reviewId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val full = reviewRepo.getFullReview(reviewId) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(full)
            }
        }
        route("/search"){
            get("/albums") {
                val query = call.request.queryParameters["query"]
                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Query parameter is required")
                    return@get
                }
                val results = albumRepo.searchAlbumsByName(query)
                call.respond(results)
            }
            get("/users") {
                val query = call.request.queryParameters["query"]
                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Query parameter is required")
                    return@get
                }
                val results = userRepo.searchUsersByUsername(query)
                call.respond(results)
            }
            get("/artists") {
                val query = call.request.queryParameters["query"]
                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Query parameter is required")
                    return@get
                }
                val results = artistRepo.searchArtistsByName(query)
                call.respond(results)
            }
        }

        // upload da imagem de perfil
        post("/upload/user-photo/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                return@post
            }

            val multipart = call.receiveMultipart()
            var imageBytes: ByteArray? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    imageBytes = part.streamProvider().readBytes()
                }
                part.dispose()
            }

            if (imageBytes != null) {
                transaction {
                    Users.update({ Users.id eq userId }) {
                        it[Users.profileImage] = ExposedBlob(imageBytes!!)
                    }
                }
                call.respond(HttpStatusCode.OK, "Profile image uploaded successfully")
            } else {
                call.respond(HttpStatusCode.BadRequest, "No image file received")
            }
        }

        // retorna a imagem de perfil
        get("/user-photo/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                return@get
            }

            val imageBlob = transaction {
                Users
                    .select { Users.id eq userId }
                    .mapNotNull { it[Users.profileImage]?.bytes }
                    .singleOrNull()
            }

            if (imageBlob != null) {
                call.respondBytes(imageBlob, ContentType.Image.JPEG)
            } else {
                call.respond(HttpStatusCode.NotFound, "No profile image found")
            }
        }

        get("/artists/{id}/albums") {
            val artistId = call.parameters["id"]?.toIntOrNull()

            if (artistId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid artist ID")
                return@get
            }

            val albums = artistRepo.getAlbumsByArtistId(artistId)

            if (albums.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No albums found for artist ID $artistId")
            } else {
                call.respond(HttpStatusCode.OK, albums)
            }
        }
    }
}
