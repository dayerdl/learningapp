package com.example.myapplication

import androidx.paging.PagingSource
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val usuarioApi: UsuarioApi
) {
    fun getUsuariosPagingSource(): PagingSource<Int, Usuario> {
        return UsuarioPagingSource(usuarioApi)
    }

    suspend fun getUsuarioById(id: Int): Result<Usuario> {
        return try {
            val usuario = usuarioApi.getUsuarioById(id)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
