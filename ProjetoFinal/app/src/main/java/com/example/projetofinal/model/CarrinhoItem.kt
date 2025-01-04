package com.example.projetofinal.model

data class CarrinhoItem(
    val produtoId: String = "",
    val nome: String = "",
    val preco: Double = 0.0,
    val quantidade: Int = 1
)
