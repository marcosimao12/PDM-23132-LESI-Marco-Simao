package com.example.projetofinal.data.repository

import com.example.projetofinal.model.Produto

class CarrinhoRepository {

    // Funções para interagir com o Firebase para gerenciar carrinhos
    fun adicionarAoCarrinho(usuarioId: String, produto: Produto) {
        // FirebaseObj.adicionarAoCarrinho() pode ser usado aqui
    }

    fun compartilharCarrinho(usuarioId: String, usuarioCompartilhadoId: String) {
        // FirebaseObj.compartilharCarrinho() pode ser usado aqui
    }

    // Métodos para acessar o banco de dados local ou Firestore
}
