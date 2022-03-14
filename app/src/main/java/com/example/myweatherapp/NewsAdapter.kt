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

open class NewsAdapter(private val newsListener: OnItemClickListener, private val sourceListener: OnSourceClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var newsList = mutableListOf<IListItem>()

    var favoriteUrlList = mutableListOf<String>()

    fun updateUrlList (list: Set<String>){ //
        favoriteUrlList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    internal fun setData(items: List<IListItem>) { // метод заполянет список новыми данными
        newsList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val item = newsList[position]
        return when (item) {
            is NewsModel -> 0
            is SourceModel -> 1
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> NewsHolder(newsListener, LayoutInflater.from(parent.context).inflate(R.layout.news_holder, parent, false))
            1 -> SourceHolder(sourceListener, LayoutInflater.from(parent.context).inflate(R.layout.source_holder, parent, false))
            else -> NewsHolder(newsListener, LayoutInflater.from(parent.context).inflate(R.layout.news_holder, parent, false))
        }
    }



    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsHolder -> holder.bind(newsList[position] as NewsModel, favoriteUrlList)
            is SourceHolder -> holder.bind(newsList[position] as SourceModel)
        }
    }
}