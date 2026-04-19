package com.fam007e.receipts.di

import android.content.Context
import androidx.room.Room
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.fam007e.receipts.data.db.ReceiptsDatabase
import com.fam007e.receipts.data.db.dao.*
import com.fam007e.receipts.data.repository.PersonRepositoryImpl
import com.fam007e.receipts.data.repository.ReceiptRepositoryImpl
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.repository.ReceiptRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ReceiptsDatabase {
        return Room.databaseBuilder(
            context,
            ReceiptsDatabase::class.java,
            "receipts_db"
        ).build()
    }

    @Provides
    fun providePersonDao(db: ReceiptsDatabase): PersonDao = db.personDao()

    @Provides
    fun provideCategoryDao(db: ReceiptsDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideReceiptDao(db: ReceiptsDatabase): ReceiptDao = db.receiptDao()

    @Provides
    fun provideAchievementDao(db: ReceiptsDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideGroupDao(db: ReceiptsDatabase): GroupDao = db.groupDao()

    @Provides
    @Singleton
    fun providePersonRepository(personDao: PersonDao): PersonRepository =
        PersonRepositoryImpl(personDao)

    @Provides
    @Singleton
    fun provideReceiptRepository(
        receiptDao: ReceiptDao,
        categoryDao: CategoryDao
    ): ReceiptRepository =
        ReceiptRepositoryImpl(receiptDao, categoryDao)

    @Provides
    @Singleton
    fun provideHttpClient(): io.ktor.client.HttpClient {
        return io.ktor.client.HttpClient(io.ktor.client.engine.okhttp.OkHttp) {
            install(ContentNegotiation) {
                json(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = io.ktor.client.plugins.logging.LogLevel.INFO
            }
        }
    }
}
