package com.example.newsapp.data.domain.model


data class News(
    val title: String,              // Título da notícia
    val abstract: String?,          // Descrição ou resumo
    val url: String,                // URL da notícia completa
    val publishedDate: String,      // Data de publicação
    val section: String            // Seção da notícia
)
