package com.beesharp.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object ArtistAlbums : Table() {
    val artistId = integer("artist_id").references(Artists.id, onDelete = ReferenceOption.CASCADE)
    val albumId = integer("album_id").references(Albums.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(artistId, albumId)
}
