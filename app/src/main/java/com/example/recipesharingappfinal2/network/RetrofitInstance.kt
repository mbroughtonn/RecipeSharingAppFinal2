package com.example.recipesharingappfinal2.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object that initializes and provides a Retrofit instance for making API requests
 * to the Spoonacular API. It includes an interceptor to append the API key to every request.
 */
object RetrofitInstance {

    /**
     * The base URL for the Spoonacular API.
     */
    private const val BASE_URL = "https://api.spoonacular.com/"

    /**
     * The API key for authenticating requests to the Spoonacular API.
     */
    private const val API_KEY = "27ad4fb1e9f142a7b530bd3e61ec36d3"

    /**
     * The OkHttpClient instance configured with an interceptor to append the API key
     * as a query parameter to every HTTP request.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("apiKey", API_KEY)
                .build()
            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
        .build()

    /**
     * The Retrofit instance configured with the base URL, a Gson converter for JSON deserialization,
     * and the customized OkHttpClient.
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    /**
     * The API service interface for accessing Spoonacular API endpoints.
     */
    val api: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}
