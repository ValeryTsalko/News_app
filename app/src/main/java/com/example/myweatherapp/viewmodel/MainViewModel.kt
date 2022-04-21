package com.example.myweatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myweatherapp.interfaces.IListItem
import com.example.myweatherapp.models.NewsModel
import com.example.myweatherapp.repository.NewsRepository


class MainViewModel(app: Application) : AndroidViewModel(app) {


    private val repository = NewsRepository()
    private var sharedPreferences: SharedPreferences = app.getSharedPreferences("Url", Context.MODE_PRIVATE)

    private val progressVisibilityLiveData = MutableLiveData<Boolean>()
    internal val getProgressVisibility = progressVisibilityLiveData

    private val newsData = MutableLiveData<List<IListItem>>()
    internal val getNewsData = newsData

    private val sourceData = MutableLiveData<List<IListItem>>()
    internal val getSourceData = sourceData

    private val favoriteData = MutableLiveData<List<IListItem>>()
    internal val getFavoriteData = favoriteData

    private var spinnerItemsMutableLiveData = MutableLiveData<Set<String>>()
    internal var getSpinnerItems = spinnerItemsMutableLiveData

    fun loadSpinnerData() {
        val spinnerList = HashSet<String>()
        spinnerList.add("all news")
        return repository.getNews("news") { list ->
            if (list!=null){
                list.forEach {
                    if (it.newsSource.name.isNotEmpty()) {
                        spinnerList.add(it.newsSource.name)
                        spinnerItemsMutableLiveData.postValue(spinnerList)
                    }
                }
                progressVisibilityLiveData.postValue(false)
            }
        }
    }

    fun loadNewsData() {
        progressVisibilityLiveData.postValue(true)
        return repository.getNews("news") { list ->
            if (list != null) {
                newsData.postValue(list)
                applyIsFavoriteState(list)
                Log.d("TAG", "GETTING NEWS DATA")
            } else {
                Log.d("TAG", "SOME ERROR")
                progressVisibilityLiveData.postValue(false)
            }
        }
    }

    fun loadSourceData() {
        progressVisibilityLiveData.postValue(true)
        return repository.getSource { list ->
            if (list != null) {
                newsData.postValue(list)
                Log.d("TAG", "GETTING SOURCE DATA")
                progressVisibilityLiveData.postValue(false)
            } else {
                Log.d("TAG", "Some ERROR")
            }
        }
    }

    fun loadFavoriteData() {
        progressVisibilityLiveData.postValue(true)
        return repository.getNews("news") { list ->
            if (list != null) {
                val favoriteKeyUrls = sharedPreferences.getStringSet(KEY_URL, emptySet()) ?: emptySet()
                val filteredData = list.filter { item ->
                    favoriteKeyUrls.any {
                        item.newsUrl == it
                    }
                }
                applyIsFavoriteState(list)
                favoriteData.postValue(filteredData)
                Log.d("TAG", "GETTING NEWS DATA")
                progressVisibilityLiveData.postValue(false)
            } else {
                Log.d("TAG", "SOME ERROR")
            }
        }
    }

    fun applyIsFavoriteState(list: List<NewsModel>) {
        val favoriteState = sharedPreferences.getStringSet(KEY_URL, emptySet()) ?: emptySet()
        list.forEach { item ->
            item.isFavorite =
                favoriteState.any { url ->
                    url == item.newsUrl
                }
        }
    }

    internal fun addNewsToFavoriteList(url: String) {
        val prefsKeys = sharedPreferences.getStringSet(KEY_URL, emptySet())
        val mutableKeySet = mutableSetOf<String>()

        sharedPreferences.edit()?.let { editor ->
            if (prefsKeys.isNullOrEmpty()) {
                editor.putStringSet(KEY_URL, setOf(url))
            } else {
                mutableKeySet.apply {
                    addAll(prefsKeys)
                    add(url)
                    Log.d("TAG", "ADDING NEWS TO FAVORITE LIST")
                }
                editor.putStringSet(KEY_URL, mutableKeySet)
            }
        }?.apply()
    }

    internal fun deleteNewsFromFavoriteList(url: String) {
        val prefsKeys = sharedPreferences.getStringSet(KEY_URL, emptySet()) ?: emptySet()
        val mutableFavoriteNews = mutableSetOf<String>()

        sharedPreferences.edit()?.let { editor ->
            mutableFavoriteNews.apply {
                addAll(prefsKeys)
                remove(url)
            }
            editor.putStringSet(KEY_URL, mutableFavoriteNews).apply()
        }

        repository.getNews("news") { list ->
            if (list != null) {
                val filteredData = list.filter { item ->
                    mutableFavoriteNews.any {
                        item.newsUrl == it
                    }
                }
                applyIsFavoriteState(filteredData)
                newsData.postValue(filteredData)
            } else {
                Log.d("TAG", "Favorite list is empty")
            }
        }
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
    }

}
