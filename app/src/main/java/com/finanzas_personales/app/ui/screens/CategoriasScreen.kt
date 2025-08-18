package com.finanzas_personales.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finanzas_personales.app.data.Categoria
import com.finanzas_personales.app.viewmodel.CategoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
	categoriaViewModel: CategoriaViewModel,
	onBackClick: () -> Unit
) {
	val allCategorias by categoriaViewModel.allActiveCategorias.collectAsState()
	val currentCategoria by categoriaViewModel.currentCategoria.collectAsState()

	var showAddEditDialog by remember { mutableStateOf(false) }
	var categoriaNombreInput by remember { mutableStateOf("") }
	var isEditMode by remember { mutableStateOf(false) }

	LaunchedEffect(currentCategoria) {
		currentCategoria?.let {
			categoriaNombreInput = it.nombre
			isEditMode = true
			showAddEditDialog = true
		} ?: run {
			categoriaNombreInput = ""
			isEditMode = false
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Gestionar Categorías") },
				navigationIcon = {
					IconButton(onClick = onBackClick) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
					}
				}
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = {
				categoriaViewModel.clearCurrentCategoria()
				showAddEditDialog = true
			}) {
				Icon(Icons.Filled.Add, "Añadir Categoría")
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp)
		) {
			if (allCategorias.isEmpty()) {
				Text(
					"No hay categorías registradas. ¡Añade una!",
					modifier = Modifier.fillMaxWidth(),
					textAlign = androidx.compose.ui.text.style.TextAlign.Center
				)
			} else {
				LazyColumn(modifier = Modifier.fillMaxSize()) {
					items(allCategorias) { categoria ->
						CategoriaItem(
							categoria = categoria,
							onEditClick = { categoriaViewModel.loadCategoriaForEdit(it) },
							onDeleteClick = { categoriaViewModel.deleteCategoria(it) }
						)
						HorizontalDivider()
					}
				}
			}
		}

		if (showAddEditDialog) {
			AlertDialog(
				onDismissRequest = { showAddEditDialog = false },
				title = { Text(if (isEditMode) "Editar Categoría" else "Añadir Nueva Categoría") },
				text = {
					OutlinedTextField(
						value = categoriaNombreInput,
						onValueChange = { categoriaNombreInput = it },
						label = { Text("Nombre de Categoría") },
						modifier = Modifier.fillMaxWidth()
					)
				},
				confirmButton = {
					TextButton(onClick = {
						if (categoriaNombreInput.isNotBlank()) {
							if (isEditMode && currentCategoria != null) {
								categoriaViewModel.updateCategoria(currentCategoria!!.copy(nombre = categoriaNombreInput))
							} else {
								categoriaViewModel.addCategoria(categoriaNombreInput)
							}
							showAddEditDialog = false
							categoriaNombreInput = ""
						}
					}) { Text(if (isEditMode) "Actualizar" else "Guardar") }
				},
				dismissButton = {
					TextButton(onClick = {
						showAddEditDialog = false
						categoriaNombreInput = ""
						categoriaViewModel.clearCurrentCategoria()
					}) { Text("Cancelar") }
				}
			)
		}
	}
}

@Composable
fun CategoriaItem(
	categoria: Categoria,
	onEditClick: (Categoria) -> Unit,
	onDeleteClick: (Categoria) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = categoria.nombre,
			modifier = Modifier.weight(1f)
		)
		IconButton(onClick = { onEditClick(categoria) }) {
			Icon(Icons.Filled.Edit, "Editar Categoría", tint = MaterialTheme.colorScheme.primary)
		}
		IconButton(onClick = { onDeleteClick(categoria) }) {
			Icon(Icons.Filled.Delete, "Eliminar Categoría", tint = MaterialTheme.colorScheme.error)
		}
	}
}