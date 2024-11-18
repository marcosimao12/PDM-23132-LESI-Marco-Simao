package com.example.newsapp.data.domain.use_case

import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.rep.NewsRepository

class GetTopStoriesUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(): List<News> {
        return repository.getTopStories()
    }
}