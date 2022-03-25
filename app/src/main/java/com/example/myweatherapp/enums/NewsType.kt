package com.example.myweatherapp.enums

import com.google.gson.annotations.SerializedName

enum class NewsType {
    @SerializedName ("business")
    BUSINESS,
    @SerializedName("entertainment")
    ENTERTAINMENT,
    @SerializedName ("general")
    GENERAL,
    @SerializedName("health")
    HEALTH,
    @SerializedName("science")
    SCIENCE,
    @SerializedName("sports")
    SPORTS,
    @SerializedName("technology")
    TECHNOLOGY,
}
