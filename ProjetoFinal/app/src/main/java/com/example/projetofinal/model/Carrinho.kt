package com.example.projetofinal.model

data class Carrinho(
    val id: String = "",
    val ownerEmail: String = "",
    val authorizedEmails: List<String> = emptyList(),
    val itens: List<CarrinhoItem> = emptyList()
)


