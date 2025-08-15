package com.finanzas_personales.app.repository

import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.database.MovimientoDao
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(private val movimientoDao: MovimientoDao) {

  val allMovimientos: Flow<List<Movimiento>> = movimientoDao.getAllMovimientos()
  val totalIngresos: Flow<Double?> = movimientoDao.getTotalIngresos()
  val totalEgresos: Flow<Double?> = movimientoDao.getTotalEgresos()

  suspend fun insert(movimiento: Movimiento) {
    movimientoDao.insert(movimiento)
  }

  suspend fun update(movimiento: Movimiento) {
    movimientoDao.update(movimiento)
  }

  suspend fun delete(movimiento: Movimiento) {
    movimientoDao.delete(movimiento)
  }

  suspend fun getMovimientoById(id: Int): Movimiento? {
    return movimientoDao.getMovimientoById(id)
  }

  fun getMovimientosByTipo(tipo: String): Flow<List<Movimiento>> {
    return movimientoDao.getMovimientosByTipo(tipo)
  }

  fun getMovimientosByCategoria(categoria: String): Flow<List<Movimiento>> {
    return movimientoDao.getMovimientosByCategoria(categoria)
  }
}
