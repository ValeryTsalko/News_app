package com.example.myweatherapp.models

import com.example.myweatherapp.interfaces.IListItem

data class NewsModel(
   val newsSource: Source,
   val newsAuthor: String?,
   val newsTitle: String,
   val newsDescription: String,
   val newsUrl: String,
   val newsUrlToImage: String,
   val newsPublishedAt: String,
   val newsContent: String,
): IListItem

