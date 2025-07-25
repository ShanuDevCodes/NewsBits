package com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
abstract class AppDatabase: RoomDatabase() {
    abstract fun RoomDao(): RoomDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "saved_article.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `History` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `query` TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}