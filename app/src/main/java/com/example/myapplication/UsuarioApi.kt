package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {
    @GET("usuarios")
    suspend fun getUsuarios(
        @Query("page") page: Int,
        @Query("size") pageSize: Int
    ): UsuarioResponse

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): Usuario
}


data class UsuarioResponse(
    val usuarios: List<Usuario>
)