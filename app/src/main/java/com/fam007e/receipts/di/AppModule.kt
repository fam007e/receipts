package com.fam007e.receipts.di

import android.content.Context
import androidx.room.Room
import com.fam007e.receipts.data.db.ReceiptsDatabase
import com.fam007e.receipts.data.db.dao.*
import com.fam007e.receipts.data.repository.PersonRepositoryImpl
import com.fam007e.receipts.data.repository.ReceiptRepositoryImpl
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.google.ai.client.generativeai.GenerativeModel
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
    @Singleton
    fun providePersonRepository(personDao: PersonDao): PersonRepository =
        PersonRepositoryImpl(personDao)

    @Provides
    @Singleton
    fun provideReceiptRepository(receiptDao: ReceiptDao): ReceiptRepository =
        ReceiptRepositoryImpl(receiptDao)

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        val apiKey = System.getenv("GEMINI_API_KEY") ?: ""
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }
}
