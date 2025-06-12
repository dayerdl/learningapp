package com.example.myapplication

import androidx.paging.PagingSource
import androidx.paging.PagingState

class UsuarioPagingSource(
    private val api: UsuarioApi
) : PagingSource<Int, Usuario>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Usuario> {
        val page = params.key ?: 1
        return try {
            val response = api.getUsuarios(page = page, pageSize = params.loadSize)
            LoadResult.Page(
                data = response.usuarios,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.usuarios.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Usuario>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}
