package com.example.newsapp.data.remote.api

import com.example.newsapp.data.remote.model.NewsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApi {
    @GET("/technology.json")
    suspend fun getTopStories( ): List<NewsDto>
}