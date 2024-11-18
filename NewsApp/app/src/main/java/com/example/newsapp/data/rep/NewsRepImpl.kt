package com.example.newsapp.data.rep

import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.model.NewsApiResponse
import com.example.newsapp.data.domain.rep.NewsRepository
import com.example.newsapp.data.remote.api.NewsApi

class NewsRepositoryImpl(private val api: NewsApi) : NewsRepository {
    override suspend fun getTopStories(): NewsApiResponse {
        return api.getTopStories().toNewsN()
       /* return api.getTopStories().map { it.toNews() }

        */
    }
}

