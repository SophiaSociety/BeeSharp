package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table

object AlbumFavorites : Table() {
    val albumId1 = reference("album_id1", Albums.id).nullable()
    val albumId2 = reference("album_id2", Albums.id).nullable()
    val albumId3 = reference("album_id3", Albums.id).nullable()
    val albumId4 = reference("album_id4", Albums.id).nullable()
    val userId = reference("user_id", Users.id)
}

data class AlbumFavorite(
    val userId: Int,
    val albumIds: List<Int?>
)
