package com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.data.dao.RoomDao
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.History
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.SavedArticle

@Database(
    entities = [SavedArticle::class, History::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun RoomDao(): RoomDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `History` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `query` TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}