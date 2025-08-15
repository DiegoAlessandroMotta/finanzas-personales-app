package com.finanzas_personales.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.viewmodel.MovimientoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
        viewModel: MovimientoViewModel,
        movimientoId: Int?,
        onBackClick: () -> Unit
) {
  val scope = rememberCoroutineScope()
  var tipo by remember { mutableStateOf("Ingreso") } // "Ingreso" o "Egreso"
  var monto by remember { mutableStateOf("") }
  var categoria by remember { mutableStateOf("") }
  var descripcion by remember { mutableStateOf("") }

  var originalMovimientoId: Int? by remember { mutableStateOf(null) }

  LaunchedEffect(movimientoId) {
    if (movimientoId != null && movimientoId != 0) {
      viewModel.loadMovimientoForEdit(movimientoId)
    } else {
      viewModel.clearCurrentMovimiento()
    }
  }

  // Observar el movimiento actual desde el ViewModel
  val currentMovimiento by viewModel.currentMovimiento.collectAsState()

  // Rellenar campos si currentMovimiento cambia (cuando se carga para edición)
  LaunchedEffect(currentMovimiento) {
    currentMovimiento?.let { mov ->
      originalMovimientoId = mov.id
      tipo = mov.tipo
      monto = mov.monto.toString()
      categoria = mov.categoria
      descripcion = mov.descripcion
    }?: run {
      originalMovimientoId = null
    }
  }

  Scaffold(
          topBar = {
            TopAppBar(
                    title = {
                      Text(
                              if (movimientoId == null || movimientoId == 0) "Nuevo Movimiento"
                              else "Editar Movimiento"
                      )
                    },
                    navigationIcon = {
                      IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                      }
                    }
            )
          }
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
      // Selector de Tipo (Ingreso/Egreso)
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        FilterChip(
          selected = tipo == "Ingreso",
          onClick = { tipo = "Ingreso" },
          label = { Text("Ingreso") },
          modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
        )
        FilterChip(
          selected = tipo == "Egreso",
          onClick = { tipo = "Egreso" },
          label = { Text("Egreso") },
          modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
              value = monto,
              onValueChange = { newValue ->
                // Validar que solo se ingresen números y un solo punto decimal
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                  monto = newValue
                }
              },
              label = { Text("Monto (S/.)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
              value = categoria,
              onValueChange = { categoria = it },
              label = { Text("Categoría (ej: Salario, Comida, Transporte)") },
              modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
              value = descripcion,
              onValueChange = { descripcion = it },
              label = { Text("Descripción") },
              modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))

      Button(
              onClick = {
                val currentMonto = monto.toDoubleOrNull()
                if (currentMonto != null &&
                                currentMonto > 0 &&
                                categoria.isNotBlank() &&
                                descripcion.isNotBlank()
                ) {
                  val newMovimiento =
                          Movimiento(
                              id = originalMovimientoId ?: 0,
                              tipo = tipo,
                              monto = currentMonto,
                              categoria = categoria,
                              descripcion = descripcion,
                              fecha = currentMovimiento?.fecha ?: System.currentTimeMillis()
                          )
                  if (movimientoId == null || movimientoId == 0) {
                    viewModel.addMovimiento(newMovimiento)
                  } else {
                    viewModel.updateMovimiento(newMovimiento)
                  }
                  onBackClick()
                } else {
                  // scope.launch {
                    // ScaffoldState.snackbarHostState.showSnackbar(message = "Rellena todos los campos")
                    // val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                    //     message = "Por favor, rellena todos los campos y asegúrate de que el monto sea válido.",
                    //     actionLabel = "Ok"
                    // )
                  // }
                }
              },
              modifier = Modifier.fillMaxWidth()
      ) {
        Text(
                if (movimientoId == null || movimientoId == 0) "Guardar Movimiento"
                else "Actualizar Movimiento"
        )
      }
    }
  }
}
