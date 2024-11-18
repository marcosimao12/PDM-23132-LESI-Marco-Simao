package com.example.newsapp.data.rep

import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.rep.NewsRepository
import com.example.newsapp.data.remote.api.NewsApi

class NewsRepositoryImpl(private val api: NewsApi) : NewsRepository {
    override suspend fun getTopStories(): List<News> {
        val response = api.getTopStories()
       /* return api.getTopStories().map { it.toNews() }

        */
        return response.results.map { it.toNews() }
    }
}

