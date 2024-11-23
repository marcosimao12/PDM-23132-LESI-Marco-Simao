package com.example.newsapp.data.remote.api

import com.example.newsapp.data.remote.model.NewsApiResponseDto
import retrofit2.http.GET

interface NewsApi {
    @GET("svc/topstories/v2/technology.json")
    suspend fun getTopStories( ): NewsApiResponseDto
}
