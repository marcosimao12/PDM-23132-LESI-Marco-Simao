package com.example.projetofinal.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.projetofinal.model.Produto

@Dao
interface ProdutoDao {

    @Insert
    suspend fun inserirProduto(produto: Produto)

    @Query("SELECT * FROM produto")
    suspend fun getTodosProdutos(): List<Produto>

    @Delete
    suspend fun removerProduto(produto: Produto)
}
