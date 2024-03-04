package com.greildev.core.app.di

import android.content.Context
import androidx.room.Room
import com.greildev.core.BuildConfig
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.utils.CoreConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ErdmoveeDatabase {
        val passphrase: ByteArray = SQLiteDatabase.getBytes(BuildConfig.LIBRARY_PACKAGE_NAME.toCharArray())
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(
            context.applicationContext,
            ErdmoveeDatabase::class.java, CoreConstant.DB_NAME)
            .fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}