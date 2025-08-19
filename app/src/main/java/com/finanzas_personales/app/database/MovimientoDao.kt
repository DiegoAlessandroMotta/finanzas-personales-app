package com.finanzas_personales.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.data.MovimientoCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(movimiento: Movimiento)

  @Update suspend fun update(movimiento: Movimiento)

  @Delete suspend fun delete(movimiento: Movimiento)

  @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
  fun getAllMovimientos(): Flow<List<Movimiento>>

  @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
  fun getAllMovimientosCategorias(): Flow<List<MovimientoCategoria>>

  @Query("SELECT * FROM movimientos WHERE id = :id")
  suspend fun getMovimientoById(id: Int): Movimiento?

  @Query("SELECT * FROM movimientos WHERE tipo = :tipo ORDER BY fecha DESC")
  fun getMovimientosByTipoCategorias(tipo: String): Flow<List<MovimientoCategoria>>

  @Query("SELECT * FROM movimientos WHERE categoriaId = :categoriaId ORDER BY fecha DESC")
  fun getMovimientosByCategoriaIdCategorias(categoriaId: Int): Flow<List<MovimientoCategoria>>

  @Query("SELECT * FROM movimientos WHERE tipo = :tipo ORDER BY fecha DESC")
  fun getMovimientosByTipo(tipo: String): Flow<List<Movimiento>>

  @Query("SELECT * FROM movimientos WHERE categoriaId = :categoriaId ORDER BY fecha DESC")
  fun getMovimientosByCategoria(categoriaId: Int): Flow<List<Movimiento>>

  @Query("SELECT SUM(monto) FROM movimientos WHERE tipo = 'Ingreso'")
  fun getTotalIngresos(): Flow<Double?>

  @Query("SELECT SUM(monto) FROM movimientos WHERE tipo = 'Egreso'")
  fun getTotalEgresos(): Flow<Double?>
}
