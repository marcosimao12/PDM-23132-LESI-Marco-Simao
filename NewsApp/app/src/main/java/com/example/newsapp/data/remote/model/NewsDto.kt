package com.example.newsapp.data.remote.model

import com.example.newsapp.data.domain.model.News

/*
data class NewsDto(
    val title: String,
    val abstract: String?,
    val url: String,
    val published_date: String,
    val section: String

) {

    fun toNews(): News {
        return News(
            title = title,
            abstract = abstract,
            url = url,
            publishedDate = published_date,
            section = section,
        )
    }
}

 */


// Representing the full API response
data class NewsApiResponse(
    val status: String,
    val copyright: String,
    val section: String,
    val last_updated: String,
    val num_results: Int,
    val results: List<NewsDto>
) {
    // adicionar o toNews...
}

// Representing each individual article in the results
data class NewsDto(
    val section: String,
    val title: String,
    val abstract: String?,
    val url: String,
    val published_date: String,
    val byline: String
) {
    fun toNews(): News {
        return News(
            title = title,
            abstract = abstract,
            url = url,
            publishedDate = published_date,
            section = section
        )
    }
}
