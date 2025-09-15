package com.greildev.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckoutMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCheckoutItem(items: List<CheckoutMovieListEntities>)

    @Query("DELETE FROM checkout_movie_entities WHERE checkout_movie_entities.id IN (:id)")
    suspend fun deleteById(id: List<Int>)

    @Query("SELECT * FROM checkout_movie_entities")
    fun getAllItems(): Flow<List<CheckoutMovieListEntities>>
}