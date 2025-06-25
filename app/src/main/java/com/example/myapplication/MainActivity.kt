package com.example.myapplication

// Application class
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
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
    val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Usuarios", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(lazyPagingItems.itemCount) { index ->
                val usuario = lazyPagingItems[index]
                if (usuario != null) {
                    UsuarioRow(usuario, onClick = { navigateToDetail(usuario.id) })
                    HorizontalDivider()
                } else {
                    PlaceholderRow()
                }
            }

            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        // Primera carga o refresco
                        item { LoadingItem() }
                    }
                    loadState.append is LoadState.Loading -> {
                        // Carga de página siguiente
                        item { LoadingItem() }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(message = e.error.localizedMessage ?: "Error desconocido") {
                                retry()
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = loadState.append as LoadState.Error
                        item {
                            ErrorItem(message = e.error.localizedMessage ?: "Error desconocido") {
                                retry()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorItem(message: String = "Ocurrió un error", onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}



@Composable
fun DetailScreen(
    userId: Int,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val usuario by viewModel.usuarioDetalle.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUsuarioById(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            usuario != null -> {
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
            }
            else -> {
                Text("Cargando usuario...")
            }
        }
    }
}

@Composable
fun UsuarioRow(usuario: Usuario, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
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

@Composable
fun PlaceholderRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingItem() {
    Text("Cargando más usuarios...", modifier = Modifier.padding(16.dp))
}

@Composable
fun ErrorItem() {
    Text("Error al cargar más usuarios", modifier = Modifier.padding(16.dp))
}




@HiltAndroidApp
class MyApplication : Application()
