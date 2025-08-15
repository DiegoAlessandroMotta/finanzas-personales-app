package com.finanzas_personales.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzas_personales.app.data.Movimiento
import com.finanzas_personales.app.data.MovimientoType
import com.finanzas_personales.app.viewmodel.MovimientoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        viewModel: MovimientoViewModel,
        onAddClick: () -> Unit,
        onEditClick: (Int) -> Unit
) {
  // val allMovimientos by viewModel.allMovimientos.collectAsState()
  val totalIngresos by viewModel.totalIngresos.collectAsState()
  val totalEgresos by viewModel.totalEgresos.collectAsState()
  val filteredMovimientos by viewModel.filteredMovimientos.collectAsState()

  Scaffold(
          topBar = { TopAppBar(title = { Text(
            text = "Finanzas Personales",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          ) }) },
          floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
              Icon(Icons.Filled.Add, "Añadir nuevo movimiento")
            }
          }
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
      Card(
              modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
                  "Resumen",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Ingresos:", fontWeight = FontWeight.SemiBold)
            Text(
                    "S/ ${"%.2f".format(totalIngresos ?: 0.0)}",
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Egresos:", fontWeight = FontWeight.SemiBold)
            Text(
                    "S/ ${"%.2f".format(totalEgresos ?: 0.0)}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(4.dp))
          Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Saldo Neto:", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            val saldoNeto = (totalIngresos ?: 0.0) - (totalEgresos ?: 0.0)
            Text(
                    "S/ ${"%.2f".format(saldoNeto)}",
                    color = if (saldoNeto >= 0) Color.White else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
            )
          }
        }
      }

      // Text("Filtros aquí...", modifier = Modifier.padding(bottom = 8.dp))

      Text(
              "Movimientos Recientes",
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier.padding(bottom = 8.dp)
      )

      if (filteredMovimientos.isEmpty()) {
        Text(
                "No hay movimientos registrados. ¡Añade uno!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(filteredMovimientos) { movimiento ->
            MovimientoItem(
                    movimiento = movimiento,
                    onEditClick = { onEditClick(movimiento.id) },
                    onDeleteClick = { viewModel.deleteMovimiento(movimiento) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MovimientoItem(movimiento: Movimiento, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
  Card(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = movimiento.descripcion, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
                text = "Categoría: ${movimiento.categoria}",
                style = MaterialTheme.typography.bodySmall
        )
        Text(text = formatDate(movimiento.fecha), style = MaterialTheme.typography.bodySmall)
      }
      Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        val textColor = if (movimiento.tipo == MovimientoType.INGRESO) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
        Text(
                text =
                        "${if (movimiento.tipo == MovimientoType.INGRESO) "+" else "-"} S/ ${"%.2f".format(movimiento.monto)}",
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                fontSize = 18.sp
        )
        Row {
          IconButton(onClick = onEditClick) {
            Icon(Icons.Filled.Edit, "Editar", tint = Color.LightGray)
          }
          IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, "Eliminar", tint = Color.LightGray)
          }
        }
      }
    }
  }
}

fun formatDate(timestamp: Long): String {
  val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
  return sdf.format(Date(timestamp))
}
