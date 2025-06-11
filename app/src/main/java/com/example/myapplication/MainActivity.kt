package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
// Application class
import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import coil.compose.rememberAsyncImagePainter
import retrofit2.converter.gson.GsonConverterFactory

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
                HomeScreen(navigateToDetail = {
                    navController.navigate("detail")
                })
            }
            composable("detail") {
                DetailScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(navigateToDetail: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    val usuarios by viewModel.usuarios.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Usuarios", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(usuarios) { usuario ->
                Row(modifier = Modifier.padding(8.dp)) {
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

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = navigateToDetail) {
            Text("Ir al detalle")
        }
    }
}

@Composable
fun DetailScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("This is the detail screen")
    }
}

// ViewModel


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") // "10.0.2.2" si usas el emulador de Android
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(UsuarioApi::class.java)

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    init {
        viewModelScope.launch {
            try {
                val users =  api.getUsuarios()
                _usuarios.value = users
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


@HiltAndroidApp
class MyApplication : Application()
