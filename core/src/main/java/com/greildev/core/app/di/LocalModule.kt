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
internal class LocalModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ErdmoveeDatabase {
//        val password = BuildConfig.LIBRARY_PACKAGE_NAME.toByteArray()
//        val factory = SupportOpenHelperFactory(password)
        return Room.databaseBuilder(
            context.applicationContext,
            ErdmoveeDatabase::class.java, CoreConstant.DB_NAME)
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
//            .openHelperFactory(factory)
            .build()
    }
}
