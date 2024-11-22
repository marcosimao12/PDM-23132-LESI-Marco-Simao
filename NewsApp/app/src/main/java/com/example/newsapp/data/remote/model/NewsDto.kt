package com.example.newsapp.data.remote.model

import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.model.NewsApiResponse

data class NewsApiResponseDto(
    val status: String,
    val copyright: String,
    val section: String,
    val last_updated: String,
    val num_results: Int,
    val results: List<NewsDto>
) {
    fun toNewsN(): NewsApiResponse {
        return NewsApiResponse(
            status = status,
            copyright = copyright,
            section = section,
            last_updated = last_updated,
            num_results = num_results,
            results = results.map{ it.toNews() }
        )

    }
}

data class NewsDto(
    val section: String,
    val title: String,
    val abstract: String?,
    val url: String,
    val published_date: String,
    val byline: String,
    val multimedia: List<MultimediaDto>?
) {
    fun toNews(): News {
        return News(
            title = title,
            abstract = abstract,
            url = url,
            publishedDate = published_date,
            section = section,
            byline = byline,
            imageUrl = multimedia?.firstOrNull()?.url
        )
    }
}
data class MultimediaDto(
    val url: String // Campo de URL para imagem
)
