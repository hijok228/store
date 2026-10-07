package com.example.fefustore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun categoryDao(): CategoryDao

    abstract fun cartDao(): CartDao

    companion object {

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(
                database: SupportSQLiteDatabase
            ) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cart_items (
                        productId TEXT NOT NULL,
                        sizeId TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        PRIMARY KEY(productId, sizeId)
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}