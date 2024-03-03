package com.greildev.core.data.source.local

import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.preferences.PreferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val database: ErdmoveeDatabase
) {

    private val favoriteMovieDao = database.favoriteMovieDao()
    private val cartMovieDao = database.cartMovieDao()

    //Onboarding
    fun userOnboardingPreferences() = preferencesDataStore.getUserOnboardingPreferences()
    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        preferencesDataStore.saveUserOnboardingPreferences(isShowOnboarding)
    }

    //favorite movie
    fun getFavoriteMoviesByUid(uid: String) = favoriteMovieDao
        .getFavoriteMovieByUid(uid)
        .flowOn(Dispatchers.IO)
    suspend fun saveFavoriteMovie(favoriteMovieListEntities: FavoriteMovieListEntities) {
        favoriteMovieDao.insertFavoriteMovie(favoriteMovieListEntities)
    }
    suspend fun deleteFavoriteMovie(favoriteId: Int) {
        favoriteMovieDao.deleteNonFavoriteMovie(favoriteId)
    }

    suspend fun deleteFavoriteMovieByIdAndUid(uid: String, id: Int) {
        favoriteMovieDao.deleteNonFavoriteMovieByIdAndUid(uid, id)
    }

    suspend fun checkFavoriteMovie(id: Int, uid: String) = favoriteMovieDao.checkFavoriteMovieByIdAndUid(id, uid)

    //cart movie
    fun getCartMoviesByUid(uid: String) = cartMovieDao
        .getCartMovieByUid(uid)
        .flowOn(Dispatchers.IO)
    suspend fun saveCartMovie(cartMovieListEntities: CartMovieListEntities) {
        cartMovieDao.insertCart(cartMovieListEntities)
    }
    suspend fun deleteCartMovie(cartId: Int) {
        cartMovieDao.deleteNonCart(cartId)
    }
    suspend fun checkCartMovieByUidAndId(uid: String, id: Int) = cartMovieDao.checkCartByUidAndId(uid, id)
    suspend fun updateQuantity(cartId: Int, newQuantity: Int, newQuantityPrice: Int) {
        cartMovieDao.updateQuantity(cartId, newQuantity, newQuantityPrice)
    }
    suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        cartMovieDao.isCheckedByCartId(cartId, newIsChecked)
    }
    suspend fun deleteCheckedByUid(isChecked: Boolean, uid: String) {
        cartMovieDao.deleteCheckedByUid(isChecked, uid)
    }
    fun getCheckedCartByUid(isChecked: Boolean, uid: String) = cartMovieDao.getCheckedCartByUid(isChecked, uid)
}
