package com.finanzas_personales.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.data.MovimientoType
import com.finanzas_personales.app.viewmodel.CategoriaViewModel
import com.finanzas_personales.app.viewmodel.MovimientoViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
        movimientoViewModel: MovimientoViewModel,
        categoriaViewModel: CategoriaViewModel,
        movimientoId: Int?,
        onBackClick: () -> Unit
) {
  val context = LocalContext.current
  var tipo by remember { mutableStateOf(MovimientoType.INGRESO) }
  var monto by remember { mutableStateOf("") }
  var descripcion by remember { mutableStateOf("") }
  var originalMovimientoId: Int? by remember { mutableStateOf(null) }

  var categoriaSeleccionadaId: Int? by remember { mutableStateOf(null) }
  var categoriaSeleccionadaNombre by remember { mutableStateOf("") }
  val allCategorias by categoriaViewModel.allActiveCategorias.collectAsState()

  var expanded by remember { mutableStateOf(false) }

  var showDatePicker by remember { mutableStateOf(false) }
  var fechaSeleccionadaMillis by remember { mutableStateOf(getStartOfDayMillis(System.currentTimeMillis())) }

  LaunchedEffect(movimientoId) {
    if (movimientoId != null && movimientoId != 0) {
      movimientoViewModel.loadMovimientoForEdit(movimientoId)
    } else {
      movimientoViewModel.clearCurrentMovimiento()
    }
  }

  LaunchedEffect(allCategorias) {
    if (allCategorias.none { it.id == categoriaSeleccionadaId }) {
      val defaultCategory = allCategorias.firstOrNull()
      categoriaSeleccionadaId = defaultCategory?.id ?: 0
      categoriaSeleccionadaNombre = defaultCategory?.nombre ?: ""
    } else if (categoriaSeleccionadaNombre.isBlank() && categoriaSeleccionadaId != 0) {
      categoriaSeleccionadaNombre = allCategorias.firstOrNull { it.id == categoriaSeleccionadaId }?.nombre ?: ""
    }
  }

  val currentMovimiento by movimientoViewModel.currentMovimiento.collectAsState()

  LaunchedEffect(currentMovimiento, allCategorias) {
    currentMovimiento?.let { mov ->
      originalMovimientoId = mov.id
      tipo = mov.tipo
      monto = mov.monto.toString()
      descripcion = mov.descripcion
      fechaSeleccionadaMillis = getStartOfDayMillis(mov.fecha)

      val categoriaAsociada = allCategorias.firstOrNull { it.id == mov.categoriaId }
      categoriaSeleccionadaId = mov.categoriaId
      categoriaSeleccionadaNombre = categoriaAsociada?.nombre ?: "Categoría Desconocida"
    }?: run {
      originalMovimientoId = null
      tipo = MovimientoType.INGRESO
      descripcion = ""
      fechaSeleccionadaMillis = getStartOfDayMillis(System.currentTimeMillis())

      val defaultCategory = allCategorias.firstOrNull()
      categoriaSeleccionadaId = defaultCategory?.id ?: 0
      categoriaSeleccionadaNombre = defaultCategory?.nombre ?: ""
    }
  }

  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaSeleccionadaMillis)

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
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        FilterChip(
          selected = tipo == MovimientoType.INGRESO,
          onClick = { tipo = MovimientoType.INGRESO },
          label = { Text("Ingreso") },
          modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
        )
        FilterChip(
          selected = tipo == MovimientoType.EGRESO,
          onClick = { tipo = MovimientoType.EGRESO },
          label = { Text("Egreso") },
          modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
              value = monto,
              onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                  monto = newValue
                }
              },
              label = { Text("Monto (S/.)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))

      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = categoriaSeleccionadaNombre,
          onValueChange = { },
          readOnly = true,
          label = { Text("Categoría") },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
          },
          modifier = Modifier
            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
            .fillMaxWidth()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          if (allCategorias.isEmpty()) {
            DropdownMenuItem(
              text = { Text("No hay categorías. Crea una primero.") },
              onClick = { expanded = false },
              enabled = false
            )
          } else {
            allCategorias.forEach { categoria ->
              DropdownMenuItem(
                text = { Text(categoria.nombre) },
                onClick = {
                  categoriaSeleccionadaId = categoria.id
                  categoriaSeleccionadaNombre = categoria.nombre
                  expanded = false
                }
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
              value = descripcion,
              onValueChange = { descripcion = it },
              label = { Text("Descripción") },
              modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = Instant.ofEpochMilli(fechaSeleccionadaMillis)
          .atZone(ZoneId.systemDefault())
          .toLocalDate()
          .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
        onValueChange = { },
        label = { Text("Fecha del Movimiento") },
        readOnly = true,
        trailingIcon = {
          IconButton(onClick = { showDatePicker = true }) {
            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(16.dp))

      if (showDatePicker) {
        DatePickerDialog(
          onDismissRequest = { showDatePicker = false },
          confirmButton = {
            TextButton(
              onClick = {
                showDatePicker = false
                datePickerState.selectedDateMillis?.let { newDate ->
                  fechaSeleccionadaMillis = Instant.ofEpochMilli(newDate)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                }
              }
            ) { Text("Aceptar") }
          },
          dismissButton = {
            TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
          }
        ) {
          DatePicker(
            state = datePickerState,
            modifier = Modifier.wrapContentSize(),
          )
        }
      }

      Button(
              onClick = {
                val montoDouble = monto.toDoubleOrNull()
                if (montoDouble == null || montoDouble <= 0) {
                  Toast.makeText(
                    context,
                    "Por favor, introduce un monto válido.",
                    Toast.LENGTH_SHORT
                  )
                    .show()
                } else if (categoriaSeleccionadaId == null) {
                  Toast.makeText(context, "Debe seleccionar una categoría.", Toast.LENGTH_SHORT)
                    .show()
                } else {
                  val finalMovimientoTimestamp = combineDateWithCurrentTime(fechaSeleccionadaMillis)

                  val newMovimiento =
                    Movimiento(
                      id = originalMovimientoId ?: 0,
                      tipo = tipo,
                      monto = montoDouble,
                      categoriaId = categoriaSeleccionadaId ?: 0,
                      descripcion = descripcion,
                      fecha = currentMovimiento?.fecha ?: finalMovimientoTimestamp
                    )
                  if (movimientoId == null || movimientoId == 0) {
                    movimientoViewModel.addMovimiento(newMovimiento)
                  } else {
                    movimientoViewModel.updateMovimiento(newMovimiento)
                  }
                  Toast.makeText(context, "Movimiento guardado!", Toast.LENGTH_SHORT).show()
                  onBackClick()
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

fun getStartOfDayMillis(timestamp: Long): Long {
  val instant = Instant.ofEpochMilli(timestamp)
  val zonedDateTime = instant.atZone(ZoneId.systemDefault())

  val localMidnight = zonedDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault())

  return localMidnight.toInstant().toEpochMilli()
}

fun combineDateWithCurrentTime(dateMillis: Long): Long {
  val selectedLocalDate = Instant.ofEpochMilli(dateMillis)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

  val now = ZonedDateTime.now(ZoneId.systemDefault())

  val finalZonedDateTime = selectedLocalDate.atTime(now.toLocalTime())
    .atZone(ZoneId.systemDefault())

  return finalZonedDateTime.toInstant().toEpochMilli()
}