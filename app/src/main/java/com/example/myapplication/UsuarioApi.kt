package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface UsuarioApi {
    @GET("usuarios")
    suspend fun getUsuarios(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): List<Usuario>
}
