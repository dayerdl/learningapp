package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val userApi: UsuarioApi) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    init {
        viewModelScope.launch {
            try {
                val users = userApi.getUsuarios()
                _usuarios.value = users
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUsuarioById(id: Int): StateFlow<Usuario?> {
        return usuarios.map { list -> list.find { it.id == id } }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
    }
}

