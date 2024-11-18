package com.example.newsapp.data.rep

import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.rep.NewsRepository
import com.example.newsapp.data.remote.api.NewsApi

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {
    override suspend fun getTopStories(section: String): List<News> {
        val response = api.getTopStories(section)
        return response.results.map { it.toDomainModel() }
    }
}