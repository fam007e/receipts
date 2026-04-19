package com.fam007e.receipts.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fam007e.receipts.data.db.dao.*
import com.fam007e.receipts.data.db.entities.*

@Database(
    entities = [
        PersonEntity::class,
        CategoryEntity::class,
        ReceiptEntity::class,
        AchievementEntity::class,
        GroupEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReceiptsDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun categoryDao(): CategoryDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun achievementDao(): AchievementDao
    abstract fun groupDao(): GroupDao
}
