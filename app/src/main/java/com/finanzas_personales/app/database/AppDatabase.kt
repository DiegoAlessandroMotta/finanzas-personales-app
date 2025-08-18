package com.finanzas_personales.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.finanzas_personales.app.data.Categoria
import com.finanzas_personales.app.data.Movimiento

@Database(
  entities = [Movimiento::class, Categoria::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(MovimientoTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun movimientoDao(): MovimientoDao
  abstract fun categoriaDao(): CategoriaDao

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
                    .addTypeConverter(MovimientoTypeConverter())
                    .build()
                INSTANCE = instance
                instance
              }
    }
  }
}
