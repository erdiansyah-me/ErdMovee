package com.greildev.core.data.source.local.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.greildev.core.utils.CoreConstant.USER_PREFERENCES_DATASTORE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_DATASTORE)

class PreferencesDataStore @Inject constructor(
    app: Application
) {
    private val dataStore = app.applicationContext.dataStore

    fun getUserOnboardingPreferences(): Flow<Boolean> {
        return dataStore.data.map {
            it[ONBOARDING_KEY] ?: true
        }
    }

    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        dataStore.edit {
            it[ONBOARDING_KEY] = isShowOnboarding
        }
    }

    companion object {
        val ONBOARDING_KEY = booleanPreferencesKey("onboarding")
    }
}