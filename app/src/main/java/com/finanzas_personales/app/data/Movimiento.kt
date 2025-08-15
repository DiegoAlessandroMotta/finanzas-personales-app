package com.finanzas_personales.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class Movimiento(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val tipo: MovimientoType,
        val monto: Double,
        val categoria: String,
        val descripcion: String,
        val fecha: Long
)
