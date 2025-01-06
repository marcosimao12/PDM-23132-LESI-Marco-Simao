package com.example.newsapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.newsapp.data.domain.model.News
import com.example.newsapp.data.presentation.newscreen.newListViewModel
import com.example.newsapp.data.presentation.newscreen.newsviewmodel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "newsList"
            ) {
                composable("newsList") {
                    val newsviewmodel = newsviewmodel()
                    NewsListScreen(navController, newsviewmodel)
                }
                composable("newsDetail/{article}") { backStackEntry ->
                    val article = backStackEntry.arguments?.getString("article")
                    val viewModel = newListViewModel()
                    NewsDetailScreen(article!!, viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsListScreen(navController: NavHostController, newsviewmodel: newsviewmodel) {
    val apiData = newsviewmodel.news.collectAsState()

    if (apiData.value.results.isEmpty()) {
        LaunchedEffect(Unit) {
            newsviewmodel.fetchNews()
        }
    }

    LazyColumn {
        items(apiData.value.results) { article ->
            NewsCard(article, navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsCard(article: News, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("newsDetail/${article.uri}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                article.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .build(),
                        contentDescription = "Article Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        article.abstract?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = "Published on: ${formatDate(article.publishedDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Author: ${article.byline}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailScreen(articleUri: String, viewModel: newListViewModel) {

    val article by viewModel.new.collectAsState()

    if (article == null) {
        LaunchedEffect(Unit) {
            viewModel.fetchNew(articleUri)
        }
    }

    if (article != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            article!!.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    contentDescription = "Article Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article!!.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Published on: ${formatDate(article!!.publishedDate)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Author: ${article!!.byline}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            article!!.abstract?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        Text(
            text = "Loading article details...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun formatDate(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
    val dateTime = LocalDateTime.parse(dateString, formatter)
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    return dateTime.format(dateFormatter)
}
