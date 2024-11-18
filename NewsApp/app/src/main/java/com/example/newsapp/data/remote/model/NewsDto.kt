package com.example.newsapp.data.remote.model

import com.example.newsapp.data.domain.model.News


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