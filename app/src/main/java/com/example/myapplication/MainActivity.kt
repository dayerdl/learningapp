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
    val counter by viewModel.counter.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Counter: $counter")
        Button(onClick = { viewModel.increment() }) {
            Text("Increment")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = navigateToDetail) {
            Text("Go to Detail")
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
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    fun increment() {
        _counter.value++
    }
}



@HiltAndroidApp
class MyApplication : Application()
