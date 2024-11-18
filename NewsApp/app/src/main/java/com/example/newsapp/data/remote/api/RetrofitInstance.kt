package com.example.newsapp.data.remote.api


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL = "https://api.nytimes.com/"
    private const val API_KEY = "SahfQo4Qxc46Dk7GOc5Oed7riX8APLXG"

    val api: NewsApi by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url()
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("api-key", API_KEY)
                    .build()
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()

                chain.proceed(newRequest)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}
