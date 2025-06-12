package com.example.myapplication

// Application class
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    MaterialTheme {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navigateToDetail = { userId ->
                    navController.navigate("detail/$userId")
                })
            }
            composable(
                "detail/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                DetailScreen(userId)
            }
        }
    }
}

@Composable
fun HomeScreen(navigateToDetail: (Int) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val usuarios by viewModel.usuarios.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Usuarios", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(usuarios) { usuario ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navigateToDetail(usuario.id) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(usuario.imagen),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Nombre: ${usuario.nombre}")
                        Text("Ciudad: ${usuario.ciudad}")
                    }
                }
            }
        }
    }
}


@Composable
fun DetailScreen(userId: Int, viewModel: HomeViewModel = viewModel()) {
    val usuario by viewModel.getUsuarioById(userId).collectAsState(initial = null)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (usuario != null) {
            Text("Detalle del usuario", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(usuario!!.imagen),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("ID: ${usuario!!.id}")
            Text("Nombre: ${usuario!!.nombre}")
            Text("Ciudad: ${usuario!!.ciudad}")
        } else {
            Text("Usuario no encontrado")
        }
    }
}



@HiltAndroidApp
class MyApplication : Application()
