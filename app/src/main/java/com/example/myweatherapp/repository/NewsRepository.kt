package com.example.myweatherapp.repository

import android.util.Log
import com.example.myweatherapp.BuildConfig
import com.example.myweatherapp.interfaces.RetrofitServices
import com.example.myweatherapp.models.NewsModel
import com.example.myweatherapp.models.NewsRoot
import com.example.myweatherapp.models.SourceModel
import com.example.myweatherapp.models.SourceRoot
import com.example.myweatherapp.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.OffsetDateTime

class NewsRepository {

    private val retrofitClient: Retrofit by lazy {
        RetrofitClient.getClient()
    }

    private val retrofitService = retrofitClient.create(RetrofitServices::class.java)

    fun getSource(category: String? = null, callback: (List<SourceModel>?) -> Unit) {
        val call = retrofitService.getSourceList(
            apiKey = BuildConfig.API_KEY,
            language = "en",
            category = category
        )

        call.enqueue(object : Callback<SourceRoot> {
            override fun onResponse(call: Call<SourceRoot>, response: Response<SourceRoot>) {
                val mappedData = response.body()?.let { root ->
                    root.sources.map { source ->
                        SourceModel(
                            sourceId = source.id,
                            sourceName = source.name,
                            sourceDescription = source.description,
                            sourceUrl = source.url,
                            sourceCategory = source.category,
                            sourceLanguage = source.language,
                            sourceCountry = source.country
                        )
                    }
                }
                callback(mappedData)
            }

            override fun onFailure(call: Call<SourceRoot>, t: Throwable) {
                t.message?.let {
                    Log.d("TAG", it)
                }
                callback(null)
            }
        })
    }

    /*  fun getSearchSources(searchQuery: String, callback: (List<SourceModel>?) -> Unit) {
          val call = retrofitService.getSearchSources(
              apiKey = BuildConfig.API_KEY,
              searchQuery = searchQuery
          )
          call.enqueue(object : Callback<SourceRoot>{
              override fun onResponse(call: Call<SourceRoot>, response: Response<SourceRoot>) {
                  val mappedDate = response.body()?.let { root ->
                      root.sources.map {

                      }
                  }
              }

              override fun onFailure(call: Call<SourceRoot>, t: Throwable) {

              }

          })

      }*/

    fun getNews(searchParam: String? = null, callback: (List<NewsModel>?) -> Unit) {
        val call = retrofitService.getNewsList (
            apiKey = BuildConfig.API_KEY,
            searchParam = searchParam
        )

        call.enqueue(object : Callback<NewsRoot> {
            override fun onResponse(call: Call<NewsRoot>, response: Response<NewsRoot>) {
                val mappedData = response.body()?.let { root ->
                    root.articles.map { article ->
                        NewsModel(
                            newsSource = article.source,
                            newsAuthor = article.author,
                            newsTitle = article.title,
                            newsDescription = article.description,
                            newsUrl = article.url,
                            newsUrlToImage = article.urlToImage,
                            newsPublishedAt = OffsetDateTime.parse(article.publishedAt),
                            newsContent = article.content
                        )
                    }
                }
                callback(mappedData)
            }

            override fun onFailure(call: Call<NewsRoot>, t: Throwable) {
                t.message?.let {
                    Log.d("TAG", it)
                }
                callback(null)
            }
        })
    }
}
