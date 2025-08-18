package com.finanzas_personales.app.repository

import com.finanzas_personales.app.data.Categoria
import com.finanzas_personales.app.database.CategoriaDao
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

	val allActiveCategorias: Flow<List<Categoria>> = categoriaDao.getAllActiveCategorias()

	suspend fun insert(categoria: Categoria) {
		categoriaDao.insert(categoria)
	}

	suspend fun update(categoria: Categoria) {
		categoriaDao.update(categoria)
	}

	suspend fun softDelete(id: Int) {
		categoriaDao.softDelete(id)
	}

	suspend fun getCategoriaByName(nombre: String): Categoria? {
		return categoriaDao.getCategoriaByName(nombre)
	}

	suspend fun countMovimientosByCategoria(categoriaNombre: String): Int {
		return categoriaDao.countMovimientosByCategoria(categoriaNombre)
	}
}