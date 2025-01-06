package com.example.newsapp.data.presentation.newscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.domain.use_case.GetTopStoriesUseCase
import com.example.newsapp.data.remote.api.RetrofitInstance
import com.example.newsapp.data.rep.NewsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class newListViewModel(): ViewModel() {
    private val api = RetrofitInstance.api
    private val repository = NewsRepositoryImpl(api)
    private val getNewUseCase = GetTopStoriesUseCase(repository)

    val new = MutableStateFlow<News?>(null)


    fun fetchNew(newUri: String){
        viewModelScope.launch {
            try {
                val news = getNewUseCase()

                new.value = news.results.firstOrNull { it.uri == newUri }
            } catch (e: Exception) {
                new.value = null
            }
        }
    }

}