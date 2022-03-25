package com.example.myweatherapp.models

import com.example.myweatherapp.interfaces.IListItem

data class SourceModel (
    val sourceId: String?,
    val sourceName: String,
    val sourceDescription: String,
    val sourceUrl: String,
    val sourceCategory: String,
    val sourceLanguage: String,
    val sourceCountry: String
): IListItem()
