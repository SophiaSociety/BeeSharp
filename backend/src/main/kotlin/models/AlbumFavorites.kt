package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable

object AlbumFavorites : Table() {
    val albumId1 = integer("album_id1").nullable()
    val albumId2 = integer("album_id2").nullable()
    val albumId3 = integer("album_id3").nullable()
    val albumId4 = integer("album_id4").nullable()
    val userId = integer("user_id").references(Users.id)
    override val primaryKey = PrimaryKey(albumId1, albumId2, albumId3, albumId4, userId)
}

@Serializable
data class AlbumFavoritesEntry(
    val albumId1: Int?,
    val albumId2: Int?,
    val albumId3: Int?,
    val albumId4: Int?,
    val userId: Int
)
