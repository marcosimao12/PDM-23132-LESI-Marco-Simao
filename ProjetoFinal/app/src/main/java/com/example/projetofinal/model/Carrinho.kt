package com.example.projetofinal.model

data class CarrinhoItem(
    val produtoId: String,
    val nome: String,
    var quantidade: Int = 1
)

