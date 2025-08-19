package com.finanzas_personales.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.finanzas_personales.app.data.Categoria
import com.finanzas_personales.app.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

	val allActiveCategorias: StateFlow<List<Categoria>> = repository.allActiveCategorias.stateIn(
		viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = emptyList()
	)

	private val _currentCategoria = MutableStateFlow<Categoria?>(null)
	val currentCategoria: StateFlow<Categoria?> = _currentCategoria.asStateFlow()

	private val _message = MutableStateFlow<String?>(null)
	val message: StateFlow<String?> = _message.asStateFlow()

	fun addCategoria(nombre: String) {
		viewModelScope.launch {
			val existingCategoria = repository.getCategoriaByName(nombre)
			if (existingCategoria == null) {
				repository.insert(Categoria(nombre = nombre))
				_message.value = "Categoría '${nombre}' añadida."
			} else {
				_message.value = "Error: La categoría '${nombre}' ya existe."
			}
		}
	}

	fun updateCategoria(categoria: Categoria) {
		viewModelScope.launch {
			repository.update(categoria)
			_message.value = "Categoría '${categoria.nombre}' actualizada."
		}
	}

	fun deleteCategoria(categoria: Categoria) {
		viewModelScope.launch {
			try {
				val movimientosCount = repository.countMovimientosByCategoria(categoria.id)
				if (movimientosCount == 0) {
					repository.softDelete(categoria.id)
					_message.value = "Categoría '${categoria.nombre}' eliminada."
				} else {
					_message.value = "No se puede eliminar '${categoria.nombre}': tiene $movimientosCount movimiento(s) vinculado(s)."
				}
			} catch (e: Exception) {
				_message.value = "Error desconocido al eliminar categoría"
			}
		}
	}

	fun loadCategoriaForEdit(categoria: Categoria) {
		_currentCategoria.value = null
		_currentCategoria.value = categoria
	}

	fun clearCurrentCategoria() {
		_currentCategoria.value = null
	}
}

class CategoriaViewModelFactory(private val repository: CategoriaRepository) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return CategoriaViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}