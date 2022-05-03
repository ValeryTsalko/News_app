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

    private val newListOfData = MutableLiveData<List<IListItem>>()
    internal val getNewsListOfData = newListOfData

    private var spinnerItemsMutableLiveData = MutableLiveData<Set<String>>()
    internal var getSpinnerItems = spinnerItemsMutableLiveData

    fun loadSpinnerData() {
        val spinnerList = LinkedHashSet<String>()
        spinnerList.add("all news")
        return repository.getNews("news") { list ->
            list?.forEach {
                if (it.newsSource.name.isNotEmpty()) {
                    spinnerList.add(it.newsSource.name)
                    spinnerItemsMutableLiveData.postValue(spinnerList)
                }
            }
        }
    }

    fun loadNewsData() {
        progressVisibilityLiveData.postValue(true)
        return repository.getNews("news") { list ->
            if (list != null) {
                newListOfData.postValue(list)
                applyIsFavoriteState(list)
                Log.d("TAG", "GETTING NEWS DATA")
            } else {
                Log.d("TAG", "IMPOSSIBLE TO GET NEWS DATA")
                progressVisibilityLiveData.postValue(false)
            }
        }
    }

    fun loadSourceData() {
        progressVisibilityLiveData.postValue(true)
        return repository.getSource { list ->
            if (list != null) {
                newListOfData.postValue(list)
                Log.d("TAG", "GETTING SOURCE DATA")
                progressVisibilityLiveData.postValue(false)
            } else {
                Log.d("TAG", "IMPOSSIBLE TO GET SOURCE NEWS DATA")
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
                newListOfData.postValue(filteredData)
                Log.d("TAG", "GETTING FAVORITE NEWS DATA")
                progressVisibilityLiveData.postValue(false)
            } else {
                Log.d("TAG", "IMPOSSIBLE TO GET FAVORITE NEWS DATA")
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
        val prefsKeys = getCashedKeys()
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
        val mutableFavoriteNews = mutableSetOf<String>()

        sharedPreferences.edit()?.let { editor ->
            mutableFavoriteNews.apply {
                addAll(getCashedKeys())
                remove(url)
                Log.d("TAG", "DELETING NEWS FROM FAVORITE LIST")
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
                newListOfData.postValue(filteredData)
            } else {
                Log.d("TAG", "FAVORITE LIST IS EMPTY")
            }
        }
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
    }

    private fun getCashedKeys() : Set<String> = sharedPreferences.getStringSet(KEY_URL, emptySet()) ?: emptySet()
}
