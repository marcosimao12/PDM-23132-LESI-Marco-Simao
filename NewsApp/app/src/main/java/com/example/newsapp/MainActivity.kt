package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.newsapp.data.presentation.newscreen.newsviewmodel
import com.example.newsapp.ui.theme.NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    val newsviewmodel = newsviewmodel()

    newsviewmodel.fetchNews()

    LazyColumn{
        items(newsviewmodel.news.value) { article ->
            Text(article.title)
        }
    }
}