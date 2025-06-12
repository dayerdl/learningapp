package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    val pager = Pager(PagingConfig(pageSize = 20)) {
        usuarioRepository.getUsuariosPagingSource()
    }.flow.cachedIn(viewModelScope)

    private val _usuarioDetalle = MutableStateFlow<Usuario?>(null)
    val usuarioDetalle: StateFlow<Usuario?> = _usuarioDetalle

    fun loadUsuarioById(id: Int) {
        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuarioById(id)
            _usuarioDetalle.value = usuario
        }
    }
}

