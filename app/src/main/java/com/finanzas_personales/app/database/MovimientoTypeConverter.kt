package com.finanzas_personales.app.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.finanzas_personales.app.data.MovimientoType

@ProvidedTypeConverter
class MovimientoTypeConverter {
  @TypeConverter
  fun fromMovimientoTipo(type: MovimientoType): String {
    return type.value
  }

  @TypeConverter
  fun toMovimientoTipo(value: String): MovimientoType {
    return MovimientoType.fromString(value) ?: throw IllegalArgumentException("Tipo de movimiento desconocido: $value")
  }
}