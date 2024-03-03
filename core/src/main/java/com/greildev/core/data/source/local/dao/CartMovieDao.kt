package com.greildev.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface CartMovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCart(cart: CartMovieListEntities)

    @Query("SELECT * FROM cart_movie_entities WHERE cart_movie_entities.uid = :uid")
    fun getCartMovieByUid(uid: String): Flow<List<CartMovieListEntities>>

    @Query("DELETE FROM cart_movie_entities WHERE cart_movie_entities.cartId = :cartId")
    suspend fun deleteNonCart(cartId: Int)

    @Query("SELECT count(*) FROM cart_movie_entities WHERE cart_movie_entities.id = :id AND cart_movie_entities.uid = :uid")
    suspend fun checkCartByUidAndId(uid: String, id:Int): Int

    @Query("UPDATE cart_movie_entities SET quantityItem = :newQuantity, quantityPrice = :newQuantityPrice WHERE cart_movie_entities.cartId = :cartId")
    suspend fun updateQuantity(cartId: Int, newQuantity: Int, newQuantityPrice: Int)

    @Query("UPDATE cart_movie_entities SET isChecked = :newIsChecked WHERE cart_movie_entities.cartId = :cartId")
    suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean)

    @Query("DELETE FROM cart_movie_entities WHERE cart_movie_entities.isChecked = :isChecked AND cart_movie_entities.uid = :uid")
    suspend fun deleteCheckedByUid(isChecked: Boolean, uid: String)

    @Query("SELECT * FROM cart_movie_entities WHERE cart_movie_entities.isChecked = :isChecked AND cart_movie_entities.uid = :uid")
    fun getCheckedCartByUid(isChecked: Boolean, uid: String): Flow<List<CartMovieListEntities>>
}