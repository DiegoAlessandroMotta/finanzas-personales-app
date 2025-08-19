package com.finanzas_personales.app.data

import androidx.room.Embedded
import androidx.room.Relation

data class MovimientoCategoria(
	@Embedded val movimiento: Movimiento,
	@Relation(
		parentColumn = "categoriaId",
		entityColumn = "id"
	)
	val categoria: Categoria
)