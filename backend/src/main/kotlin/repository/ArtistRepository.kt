package com.beesharp.backend.repository

import com.beesharp.backend.models.Artist
import com.beesharp.backend.models.Artists
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class ArtistRepository {

    fun getAllArtists(): List<Artist> = transaction {
        Artists.selectAll().map {
            Artist(
                id = it[Artists.id],
                name = it[Artists.name]
            )
        }
    }

    fun getArtistById(id: Int): Artist? = transaction {
        Artists.select { Artists.id eq id }
            .map { Artist(it[Artists.id], it[Artists.name]) }
            .singleOrNull()
    }

    fun searchArtistsByName(partialName: String): List<Artist> = transaction {
        Artists.select { Artists.name.lowerCase() like "%${partialName.lowercase()}%" }
            .map { Artist(it[Artists.id], it[Artists.name]) }
    }

    fun addArtist(name: String): Int = transaction {
        Artists.insert {
            it[Artists.name] = name
        } get Artists.id
    }

    fun deleteArtist(id: Int): Boolean = transaction {
        Artists.deleteWhere { Artists.id eq id } > 0
    }
}