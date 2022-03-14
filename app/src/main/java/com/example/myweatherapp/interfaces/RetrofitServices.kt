package com.example.myweatherapp.interfaces


import com.example.myweatherapp.models.NewsRoot
import com.example.myweatherapp.models.SourceRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {
    @GET("everything")
    fun getNewsList(@Query("apiKey") apiKey: String,
                    @Query("q") device: String
    ): Call<NewsRoot>

    @GET("top-headlines/sources")
    fun getSourceList(
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query ("category") category: String? = null
    ): Call<SourceRoot>


    // REQUEST PARAMS FOR FILTER
    @GET("top-headlines/sources")
    fun getSourceSortedList(
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("category") category: String
    ): Call<SourceRoot>

}