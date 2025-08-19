package com.finanzas_personales.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas_personales.app.data.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(categoria: Categoria)

	@Update
	suspend fun update(categoria: Categoria)

	@Query("UPDATE categorias SET isDeleted = 1 WHERE id = :id")
	suspend fun softDelete(id: Int)

	@Query("SELECT * FROM categorias WHERE isDeleted = 0 ORDER BY nombre ASC")
	fun getAllActiveCategorias(): Flow<List<Categoria>>

	@Query("SELECT * FROM categorias WHERE nombre = :nombre AND isDeleted = 0 LIMIT 1")
	suspend fun getCategoriaByName(nombre: String): Categoria?

	@Query("SELECT COUNT(*) FROM movimientos WHERE categoriaId = :categoriaId")
	suspend fun countMovimientosByCategoria(categoriaId: Int): Int
}