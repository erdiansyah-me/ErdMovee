package com.greildev.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteMovie(favorite: FavoriteMovieListEntities)

    @Query("DELETE FROM favorite_movie_entities WHERE favorite_movie_entities.favoriteId = :favoriteId")
    suspend fun deleteNonFavoriteMovie(favoriteId: Int): Int

    @Query("DELETE FROM favorite_movie_entities WHERE favorite_movie_entities.id = :id AND favorite_movie_entities.uid = :uid")
    suspend fun deleteNonFavoriteMovieByIdAndUid(uid: String, id: Int): Int

    @Query("SELECT count(*) FROM favorite_movie_entities WHERE favorite_movie_entities.id = :id AND favorite_movie_entities.uid = :uid")
    suspend fun checkFavoriteMovieByIdAndUid(id: Int, uid: String): Int

    @Query("SELECT * FROM favorite_movie_entities WHERE favorite_movie_entities.uid = :uid")
    fun getFavoriteMovieByUid(uid: String): Flow<List<FavoriteMovieListEntities>>
}