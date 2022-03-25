package com.example.myweatherapp.retrofit

import com.example.myweatherapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Add converter factory for serialization and deserialization of objects.
                .build()
        }
        return retrofit!!
    }
}
