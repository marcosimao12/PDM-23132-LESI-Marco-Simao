package com.example.newsapp.data.domain.rep


import com.example.newsapp.data.domain.model.News

interface NewsRepository {
    suspend fun getTopStories(): News
}

