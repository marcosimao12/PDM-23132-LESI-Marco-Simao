package com.example.newsapp.data.remote.model


data class NewsDto(
    val title: String,
    val abstract: String?,
    val url: String,
    val published_date: String,
    val section: String

) {

    fun toDomainModel(): News {
        return News(
            title = title,
            abstract = abstract,
            url = url,
            publishedDate = published_date,
            section = section,
        )
    }
}