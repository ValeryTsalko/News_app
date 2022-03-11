package com.example.myweatherapp.models


data class SourceRoot(
    val id: String,
    val name: String,
    val sources: List<Source>
)

data class Source (
    val id: String,
    val name: String,
    val description: String,
    val url:String,
    val category: String,
    val language: String,
    val country: String
)

data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)

data class NewsRoot(
    val status: String,
    val totalResults: String,
    val articles: List<Article>
)
