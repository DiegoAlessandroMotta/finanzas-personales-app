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

	fun addCategoria(nombre: String) {
		viewModelScope.launch {
			val existingCategoria = repository.getCategoriaByName(nombre)
			if (existingCategoria == null) {
				repository.insert(Categoria(nombre = nombre))
			} else {
				// Manejar error: la categoría ya existe
				// Podrías exponer un StateFlow<String?> para mensajes de error
			}
		}
	}

	fun updateCategoria(categoria: Categoria) {
		viewModelScope.launch {
			repository.update(categoria)
		}
	}

	fun deleteCategoria(categoria: Categoria) {
		viewModelScope.launch {
			val movimientosCount = repository.countMovimientosByCategoria(categoria.nombre)
			if (movimientosCount == 0) {
				repository.softDelete(categoria.id)
			} else {
				// Manejar error: No se puede eliminar si hay movimientos vinculados
				// Podrías exponer un StateFlow<String?> para mensajes de error
			}
		}
	}

	fun loadCategoriaForEdit(categoria: Categoria) {
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