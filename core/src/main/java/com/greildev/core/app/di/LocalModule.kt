package com.greildev.core.app.di

import android.content.Context
import androidx.room.Room
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.utils.CoreConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ErdmoveeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ErdmoveeDatabase::class.java, CoreConstant.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}