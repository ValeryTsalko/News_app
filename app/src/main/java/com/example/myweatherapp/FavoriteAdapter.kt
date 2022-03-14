package com.example.myweatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.interfaces.IListItem
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.models.NewsModel
import com.example.myweatherapp.models.SourceModel
import com.example.myweatherapp.viewHolders.NewsHolder
import com.example.myweatherapp.viewHolders.SourceHolder

open class FavoriteAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<NewsHolder>() {

    var newsList = mutableListOf<IListItem>()

    var favoriteUrlList = mutableListOf<String>()

    fun updateUrlList (list: List<String>){
        favoriteUrlList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    internal fun setData(items: List<IListItem>) {
        newsList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
      return  NewsHolder(onItemClickListener, LayoutInflater.from(parent.context).inflate(R.layout.news_holder, parent, false))
    }



    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        (newsList[position] as? NewsModel)?.let {
            holder.bind(it, favoriteUrlList)
        }
    }
}