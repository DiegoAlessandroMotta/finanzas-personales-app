package com.finanzas_personales.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.data.MovimientoType
import com.finanzas_personales.app.viewmodel.MovimientoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
        viewModel: MovimientoViewModel,
        movimientoId: Int?,
        onBackClick: () -> Unit
) {
  val context = LocalContext.current
  // val scope = rememberCoroutineScope()
  var tipo by remember { mutableStateOf(MovimientoType.INGRESO) }
  var monto by remember { mutableStateOf("") }
  var categoria by remember { mutableStateOf("") }
  var descripcion by remember { mutableStateOf("") }
  var originalMovimientoId: Int? by remember { mutableStateOf(null) }

  var showDatePicker by remember { mutableStateOf(false) }
  var fechaSeleccionadaMillis by remember { mutableStateOf(getStartOfDayMillis(System.currentTimeMillis())) }

  LaunchedEffect(movimientoId) {
    if (movimientoId != null && movimientoId != 0) {
      viewModel.loadMovimientoForEdit(movimientoId)
    } else {
      viewModel.clearCurrentMovimiento()
    }
  }

  val currentMovimiento by viewModel.currentMovimiento.collectAsState()

  LaunchedEffect(currentMovimiento) {
    currentMovimiento?.let { mov ->
      originalMovimientoId = mov.id
      tipo = mov.tipo
      monto = mov.monto.toString()
      categoria = mov.categoria
      descripcion = mov.descripcion
      fechaSeleccionadaMillis = getStartOfDayMillis(mov.fecha)
    }?: run {
      originalMovimientoId = null
      tipo = MovimientoType.INGRESO
      fechaSeleccionadaMillis = getStartOfDayMillis(System.currentTimeMillis())
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
                } else if (descripcion.isBlank()) {
                  Toast.makeText(
                    context,
                    "La descripción no puede estar vacía.",
                    Toast.LENGTH_SHORT
                  )
                    .show()
                } else if (categoria.isBlank()) {
                  Toast.makeText(context, "La categoría no puede estar vacía.", Toast.LENGTH_SHORT)
                    .show()
                } else {
                  val finalMovimientoTimestamp = combineDateWithCurrentTime(fechaSeleccionadaMillis)

                  val newMovimiento =
                    Movimiento(
                      id = originalMovimientoId ?: 0,
                      tipo = tipo,
                      monto = montoDouble,
                      categoria = categoria,
                      descripcion = descripcion,
                      fecha = currentMovimiento?.fecha ?: finalMovimientoTimestamp
                    )
                  if (movimientoId == null || movimientoId == 0) {
                    viewModel.addMovimiento(newMovimiento)
                  } else {
                    viewModel.updateMovimiento(newMovimiento)
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