package it.thefedex87.networkboundresourcestest.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.thefedex87.networkboundresourcestest.data.db.Database
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database = Room
        .databaseBuilder(context, Database::class.java, "main_db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideUserDao(database: Database) = database.getUserDao()
}