package com.example.newsapp.data.domain.model


data class News(
    val title: String,              // Título da notícia
    val abstract: String?,          // Descrição ou resumo
    val url: String,                // URL da notícia completa
    val publishedDate: String,      // Data de publicação
    val section: String,
    val byline: String,
    val imageUrl: String?
)
data class NewsApiResponse(
    val status: String,
    val copyright: String,
    val section: String,
    val last_updated: String,
    val num_results: Int,
    val results: List<News>
)
