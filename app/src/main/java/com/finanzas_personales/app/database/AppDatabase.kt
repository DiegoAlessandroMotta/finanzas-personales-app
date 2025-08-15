package com.finanzas_personales.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.finanzas_personales.app.data.Movimiento

@Database(entities = [Movimiento::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun movimientoDao(): MovimientoDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE
              ?: synchronized(this) {
                val instance =
                        Room.databaseBuilder(
                                        context.applicationContext,
                                        AppDatabase::class.java,
                                        "finanzas_personales_db"
                                )
                                .build()
                INSTANCE = instance
                instance
              }
    }
  }
}
