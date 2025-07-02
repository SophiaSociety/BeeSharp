package com.beesharp.backend.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.beesharp.backend.models.Album
import com.beesharp.backend.models.Albums

class AlbumRepository {

    fun getAllAlbums(): List<Album> = transaction {
        Albums.selectAll().map {
            Album(
                id = it[Albums.id],
                title = it[Albums.title],
                artist = it[Albums.artist],
                year = it[Albums.year],
                genre = it[Albums.genre],
                duration = it[Albums.duration],
                averageRating = it[Albums.averageRating].toDouble(),
                reviewsCount = it[Albums.reviewsCount],
                totalRatings = it[Albums.totalRatings],
                image = it[Albums.image],
                description = it[Albums.description]
            )
        }
    }

    fun getAlbumById(id: Int): Album? = transaction {
        Albums.select { Albums.id eq id }
            .map {
                Album(
                    id = it[Albums.id],
                    title = it[Albums.title],
                    artist = it[Albums.artist],
                    year = it[Albums.year],
                    genre = it[Albums.genre],
                    duration = it[Albums.duration],
                    averageRating = it[Albums.averageRating].toDouble(),
                    reviewsCount = it[Albums.reviewsCount],
                    totalRatings = it[Albums.totalRatings],
                    image = it[Albums.image],
                    description = it[Albums.description]
                )
            }.singleOrNull()
    }

    fun getHotAlbums(limit: Int = 10): List<Album> = transaction {
        Albums.selectAll()
            .orderBy(Albums.reviewsCount, SortOrder.DESC)
            .limit(limit)
            .map {
                Album(
                    id = it[Albums.id],
                    title = it[Albums.title],
                    artist = it[Albums.artist],
                    year = it[Albums.year],
                    genre = it[Albums.genre],
                    duration = it[Albums.duration],
                    averageRating = it[Albums.averageRating].toDouble(),
                    reviewsCount = it[Albums.reviewsCount],
                    totalRatings = it[Albums.totalRatings],
                    image = it[Albums.image],
                    description = it[Albums.description]
                )
            }
    }
    
    fun getTopRatedAlbums(limit: Int = 100): List<Album> = transaction {
        Albums.selectAll()
            .orderBy(Albums.averageRating, SortOrder.DESC)
            .limit(limit)
            .map {
                Album(
                    id = it[Albums.id],
                    title = it[Albums.title],
                    artist = it[Albums.artist],
                    year = it[Albums.year],
                    genre = it[Albums.genre],
                    duration = it[Albums.duration],
                    averageRating = it[Albums.averageRating].toDouble(),
                    reviewsCount = it[Albums.reviewsCount],
                    totalRatings = it[Albums.totalRatings],
                    image = it[Albums.image],
                    description = it[Albums.description]
                )
            }
    }

    fun searchAlbumsByName(partialName: String): List<Album> = transaction {
        Albums.select { Albums.title.lowerCase() like "%${partialName.lowercase()}%" }
            .map {
                Album(
                    id = it[Albums.id],
                    title = it[Albums.title],
                    artist = it[Albums.artist],
                    year = it[Albums.year],
                    genre = it[Albums.genre],
                    duration = it[Albums.duration],
                    averageRating = it[Albums.averageRating].toDouble(),
                    reviewsCount = it[Albums.reviewsCount],
                    totalRatings = it[Albums.totalRatings],
                    image = it[Albums.image],
                    description = it[Albums.description]
                )
            }
    }

    fun addAlbum(title: String, artist: String?): Int = transaction {
        Albums.insert {
            it[Albums.title] = title
            it[Albums.artist] = artist
        } get Albums.id
    }

    fun deleteAlbum(id: Int): Boolean = transaction {
        Albums.deleteWhere { Albums.id eq id } > 0
    }

    fun getAlbumsByArtist(artistName: String): List<Album> = transaction {
        Albums.select { Albums.artist.lowerCase() eq artistName.lowercase() }
            .map {
                Album(
                    id = it[Albums.id],
                    title = it[Albums.title],
                    artist = it[Albums.artist],
                    year = it[Albums.year],
                    genre = it[Albums.genre],
                    duration = it[Albums.duration],
                    averageRating = it[Albums.averageRating].toDouble(),
                    reviewsCount = it[Albums.reviewsCount],
                    totalRatings = it[Albums.totalRatings],
                    image = it[Albums.image],
                    description = it[Albums.description]
                )
            }
    }

    fun updateRating(id: Int, newRating: Double, newCount: Int): Boolean = transaction {
        Albums.update({ Albums.id eq id }) {
            it[averageRating] = newRating.toBigDecimal()
            it[reviewsCount] = newCount
        } > 0
    }
}
