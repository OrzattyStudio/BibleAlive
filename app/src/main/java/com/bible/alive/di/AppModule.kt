package com.bible.alive.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bible.alive.data.local.BibleDatabase
import com.bible.alive.data.local.dao.BookDao
import com.bible.alive.data.local.dao.DevotionalDao
import com.bible.alive.data.local.dao.FavoriteDao
import com.bible.alive.data.local.dao.HighlightDao
import com.bible.alive.data.local.dao.NoteDao
import com.bible.alive.data.local.dao.StreakDao
import com.bible.alive.data.local.dao.UserReadingDao
import com.bible.alive.data.local.dao.VerseDao
import com.bible.alive.data.remote.api.BollsLifeApi
import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.data.repository.BibleRepositoryImpl
import com.bible.alive.data.repository.DevotionalRepository
import com.bible.alive.data.repository.DevotionalRepositoryImpl
import com.bible.alive.data.repository.StreakRepository
import com.bible.alive.data.repository.StreakRepositoryImpl
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.data.repository.UserPreferencesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bible_alive_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BollsLifeApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBollsLifeApi(retrofit: Retrofit): BollsLifeApi {
        return retrofit.create(BollsLifeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBibleDatabase(@ApplicationContext context: Context): BibleDatabase {
        return Room.databaseBuilder(
            context,
            BibleDatabase::class.java,
            BibleDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideVerseDao(database: BibleDatabase): VerseDao {
        return database.verseDao()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: BibleDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideUserReadingDao(database: BibleDatabase): UserReadingDao {
        return database.userReadingDao()
    }

    @Provides
    @Singleton
    fun provideStreakDao(database: BibleDatabase): StreakDao {
        return database.streakDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: BibleDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: BibleDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideHighlightDao(database: BibleDatabase): HighlightDao {
        return database.highlightDao()
    }

    @Provides
    @Singleton
    fun provideDevotionalDao(database: BibleDatabase): DevotionalDao {
        return database.devotionalDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideBibleRepository(
        api: BollsLifeApi,
        bookDao: BookDao,
        verseDao: VerseDao
    ): BibleRepository {
        return BibleRepositoryImpl(api, bookDao, verseDao)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        dataStore: DataStore<Preferences>
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideStreakRepository(
        streakDao: StreakDao
    ): StreakRepository {
        return StreakRepositoryImpl(streakDao)
    }

    @Provides
    @Singleton
    fun provideDevotionalRepository(
        devotionalDao: DevotionalDao
    ): DevotionalRepository {
        return DevotionalRepositoryImpl(devotionalDao)
    }
}
