package com.greildev.core.data.source.local

import com.greildev.core.data.source.local.preferences.PreferencesDataStore
import javax.inject.Inject

class LocalDataSource @Inject constructor(
   private val preferencesDataStore: PreferencesDataStore
) {
    fun userOnboardingPreferences() = preferencesDataStore.getUserOnboardingPreferences()
    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        preferencesDataStore.saveUserOnboardingPreferences(isShowOnboarding)
    }
}