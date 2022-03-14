package com.example.myweatherapp.interfaces

import com.example.myweatherapp.models.NewsModel

interface OnItemClickListener {
    fun onIconClick(url: String)
    fun onFavoriteIconClick(url: String)
}