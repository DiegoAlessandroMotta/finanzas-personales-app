package com.finanzas_personales.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finanzas_personales.app.database.AppDatabase
import com.finanzas_personales.app.repository.CategoriaRepository
import com.finanzas_personales.app.repository.MovimientoRepository
import com.finanzas_personales.app.ui.screens.CategoriasScreen
import com.finanzas_personales.app.ui.screens.HomeScreen
import com.finanzas_personales.app.ui.screens.MovimientoFormScreen
import com.finanzas_personales.app.ui.theme.FinanzasPersonalesTheme
import com.finanzas_personales.app.viewmodel.CategoriaViewModel
import com.finanzas_personales.app.viewmodel.CategoriaViewModelFactory
import com.finanzas_personales.app.viewmodel.MovimientoViewModel
import com.finanzas_personales.app.viewmodel.MovimientoViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FinanzasPersonalesTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val database = AppDatabase.getDatabase(applicationContext)

          val movimientoRepository = MovimientoRepository(database.movimientoDao())
          val categoriaRepository = CategoriaRepository(database.categoriaDao())

          val movimientoViewModel: MovimientoViewModel = viewModel(
            factory = MovimientoViewModelFactory(movimientoRepository)
          )
          val categoriaViewModel: CategoriaViewModel = viewModel(
            factory = CategoriaViewModelFactory(categoriaRepository)
          )

          ControlFinancieroApp(
            movimientoViewModel = movimientoViewModel,
            categoriaViewModel = categoriaViewModel
          )
        }
      }
    }
  }
}

@Composable
fun ControlFinancieroApp(
  movimientoViewModel: MovimientoViewModel,
  categoriaViewModel: CategoriaViewModel
) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = "home") {
    composable("home") {
      HomeScreen(
        movimientoViewModel = movimientoViewModel,
        categoriaViewModel = categoriaViewModel,
        onAddClick = { navController.navigate("add_movimiento") },
        onEditClick = { id -> navController.navigate("edit_movimiento/$id") },
        onManageCategoriesClick = { navController.navigate("manage_categories") }
      )
    }
    composable("add_movimiento") { backStackEntry ->
      MovimientoFormScreen(
        movimientoViewModel = movimientoViewModel,
        categoriaViewModel = categoriaViewModel,
        movimientoId = null,
        onBackClick = { navController.popBackStack() }
      )
    }
    composable(
            route = "edit_movimiento/{movimientoId}",
            arguments = listOf(navArgument("movimientoId") { type = NavType.IntType })
    ) { backStackEntry ->
      val movimientoId = backStackEntry.arguments?.getInt("movimientoId")
      MovimientoFormScreen(
        movimientoViewModel = movimientoViewModel,
        categoriaViewModel = categoriaViewModel,
        movimientoId = movimientoId,
        onBackClick = { navController.popBackStack() }
      )
    }
    composable("manage_categories") {
      CategoriasScreen(
        categoriaViewModel = categoriaViewModel,
        onBackClick = { navController.popBackStack() }
      )
    }
  }
}
