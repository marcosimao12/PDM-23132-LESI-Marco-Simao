package com.example.newsapp.data.remote.api

import com.example.newsapp.data.domain.model.NewsApiResponse
import com.example.newsapp.data.remote.model.NewsApiResponseDto
import com.example.newsapp.data.remote.model.NewsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApi {
    @GET("svc/topstories/v2/technology.json")
    suspend fun getTopStories( ): NewsApiResponseDto
}
