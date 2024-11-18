package com.example.newsapp.data.presentation.newscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.model.NewsApiResponse
import com.example.newsapp.data.domain.use_case.GetTopStoriesUseCase
import com.example.newsapp.data.remote.api.RetrofitInstance
import com.example.newsapp.data.rep.NewsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class newsviewmodel : ViewModel() {

    private val api = RetrofitInstance.api
    private val repository = NewsRepositoryImpl(api)
    private val getNewUseCase = GetTopStoriesUseCase(repository)

    val news = MutableStateFlow(NewsApiResponse(status = "", copyright = "", section = "", last_updated = "", num_results = 0, results = emptyList()))

    fun fetchNews(){
        viewModelScope.launch {
            try {
                news.value = getNewUseCase()
            } catch (e: Exception) {
                news.value = NewsApiResponse(status = "", copyright = "", section = "", last_updated = "", num_results = 0, results = emptyList())
            }
        }
    }
}
