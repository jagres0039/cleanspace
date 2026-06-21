package com.cleanspace.app.di

import android.content.Context
import androidx.room.Room
import com.cleanspace.app.data.local.CleanSpaceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): CleanSpaceDatabase = Room.databaseBuilder(
        context,
        CleanSpaceDatabase::class.java,
        "cleanspace.db",
    ).build()

    @Provides
    fun provideScanCacheDao(db: CleanSpaceDatabase) = db.scanCacheDao()
}
