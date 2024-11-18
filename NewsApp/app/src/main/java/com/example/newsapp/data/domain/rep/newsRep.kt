package com.example.newsapp.data.domain.rep


import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.model.NewsApiResponse

interface NewsRepository {
    suspend fun getTopStories(): NewsApiResponse
}

