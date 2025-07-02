package com.beesharp.backend.repository

import com.beesharp.backend.models.Artist
import com.beesharp.backend.models.Artists
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.beesharp.backend.models.Albums
import com.beesharp.backend.models.Album
import com.beesharp.backend.models.ArtistAlbums


class ArtistRepository {

    fun getAllArtists(): List<Artist> = transaction {
        Artists.selectAll().map {
            Artist(
                id = it[Artists.id],
                name = it[Artists.name],
                descricao = it[Artists.descricao],
                image = it[Artists.image]
            )
        }
    }

    fun getArtistById(id: Int): Artist? = transaction {
        Artists.select { Artists.id eq id }
            .map {
                Artist(
                    id = it[Artists.id],
                    name = it[Artists.name],
                    descricao = it[Artists.descricao],
                    image = it[Artists.image]
                )
            }.singleOrNull()
    }

    fun searchArtistsByName(partialName: String): List<Artist> = transaction {
        Artists.select { Artists.name.lowerCase() like "%${partialName.lowercase()}%" }
            .map {
                Artist(
                    id = it[Artists.id],
                    name = it[Artists.name],
                    descricao = it[Artists.descricao],
                    image = it[Artists.image]
                )
            }
    }

    fun getAlbumsByArtistId(artistId: Int): List<Album> {
        return transaction {
            (ArtistAlbums innerJoin Albums)
                .select { ArtistAlbums.artistId eq artistId }
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
    }

    fun getArtistsByAlbumId(albumId: Int): List<Artist> {
        return transaction {
            (ArtistAlbums innerJoin Artists)
                .select { ArtistAlbums.albumId eq albumId }
                .map {
                    Artist(
                        id = it[Artists.id],
                        name = it[Artists.name],
                        descricao = it[Artists.descricao],
                        image = it[Artists.image]
                    )
                }
        }
    }

}