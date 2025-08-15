package com.finanzas_personales.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.repository.MovimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimientoViewModel(private val repository: MovimientoRepository) : ViewModel() {
  val allMovimientos: StateFlow<List<Movimiento>> =
          repository.allMovimientos.stateIn(
                  viewModelScope,
                  started = SharingStarted.WhileSubscribed(5000),
                  initialValue = emptyList()
          )
  val totalIngresos: StateFlow<Double?> =
          repository.totalIngresos.stateIn(
                  viewModelScope,
                  started = SharingStarted.WhileSubscribed(5000),
                  initialValue = 0.0
          )
  val totalEgresos: StateFlow<Double?> =
          repository.totalEgresos.stateIn(
                  viewModelScope,
                  started = SharingStarted.WhileSubscribed(5000),
                  initialValue = 0.0
          )

  private val _currentMovimiento = MutableStateFlow<Movimiento?>(null)
  val currentMovimiento: StateFlow<Movimiento?> = _currentMovimiento.asStateFlow()

  fun addMovimiento(movimiento: Movimiento) {
    viewModelScope.launch { repository.insert(movimiento) }
  }

  fun updateMovimiento(movimiento: Movimiento) {
    viewModelScope.launch { repository.update(movimiento) }
  }

  fun deleteMovimiento(movimiento: Movimiento) {
    viewModelScope.launch { repository.delete(movimiento) }
  }

  fun loadMovimientoForEdit(movimientoId: Int) {
    viewModelScope.launch { _currentMovimiento.value = repository.getMovimientoById(movimientoId) }
  }

  fun clearCurrentMovimiento() {
    _currentMovimiento.value = null
  }

  private val _filteredMovimientos = MutableStateFlow<List<Movimiento>>(emptyList())
  val filteredMovimientos: StateFlow<List<Movimiento>> = _filteredMovimientos.asStateFlow()

  init {
    viewModelScope.launch { repository.allMovimientos.collect { _filteredMovimientos.value = it } }
  }

  fun filterByType(tipo: String?) {
    viewModelScope.launch {
      if (tipo.isNullOrBlank()) {
        _filteredMovimientos.value = repository.allMovimientos.first()
      } else {
        repository.getMovimientosByTipo(tipo).collect { _filteredMovimientos.value = it }
      }
    }
  }

  fun filterByCategory(categoria: String?) {
    viewModelScope.launch {
      if (categoria.isNullOrBlank()) {
        _filteredMovimientos.value = repository.allMovimientos.first()
      } else {
        repository.getMovimientosByCategoria(categoria).collect { _filteredMovimientos.value = it }
      }
    }
  }
}

class MovimientoViewModelFactory(private val repository: MovimientoRepository) :
        ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MovimientoViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST") return MovimientoViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
