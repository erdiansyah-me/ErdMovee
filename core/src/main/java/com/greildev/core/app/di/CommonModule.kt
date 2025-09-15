package com.greildev.core.app.di

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.time.TrustedTime
import com.google.android.gms.time.TrustedTimeClient
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.TrustedTimeClientAccessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {
    @Provides
    fun provideDispatcher() = DispatcherProvider()

    @Provides
    fun provideTrustedTimeClientAccessor(
        @ApplicationContext context: Context
    ): TrustedTimeClientAccessor {
        return object : TrustedTimeClientAccessor {
            override fun createClient(): Task<TrustedTimeClient> {
                return TrustedTime.createClient(context)
            }
        }
    }
}