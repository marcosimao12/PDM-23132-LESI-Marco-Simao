package com.example.projetofinal.data.repository

import com.example.projetofinal.data.dao.ProdutoDao
import com.example.projetofinal.data.firebase.FirebaseObj
import com.example.projetofinal.model.Produto

class ProdutoRepository(private val produtoDao: ProdutoDao) {


    // Método para buscar todos os produtos
    suspend fun buscarTodosProdutos(): List<Produto> {
        return produtoDao.getTodosProdutos()
    }

    // Método para adicionar produto ao banco local
    suspend fun adicionarProdutoNoBanco(produto: Produto) {
        produtoDao.inserirProduto(produto)
    }
}
