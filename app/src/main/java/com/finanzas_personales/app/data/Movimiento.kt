package com.finanzas_personales.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "movimientos",
        foreignKeys = [
                ForeignKey(
                        entity = Categoria::class,
                        parentColumns = ["id"],
                        childColumns = ["categoriaId"],
                        onDelete = ForeignKey.RESTRICT
                )
        ],
        indices = [Index(value = ["categoriaId"])]
)
data class Movimiento(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val tipo: MovimientoType,
        val monto: Double,
        val categoriaId: Int,
        val descripcion: String = "",
        val fecha: Long
)
