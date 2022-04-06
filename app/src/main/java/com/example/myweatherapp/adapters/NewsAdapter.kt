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

class NewsAdapter(private val newsListener: OnItemClickListener, private val sourceListener: OnSourceClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val newsList = mutableListOf<IListItem>()

    internal fun setData(items: List<IListItem>) {
        newsList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (newsList[position]) {
            is NewsModel -> NEWS_TYPE
            is SourceModel -> SOURCE_TYPE
            else -> NEWS_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NEWS_TYPE -> NewsHolder(newsListener,
                sourceListener,
                LayoutInflater.from(parent.context).inflate(R.layout.news_holder, parent, false))
            SOURCE_TYPE -> SourceHolder(LayoutInflater.from(parent.context).inflate(R.layout.source_holder, parent, false))
            else -> NewsHolder(newsListener,
                sourceListener,
                LayoutInflater.from(parent.context).inflate(R.layout.news_holder, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsHolder -> holder.bind(newsList[position] as NewsModel)
            is SourceHolder -> holder.bind(newsList[position] as SourceModel)
        }
    }

    private companion object {
        const val NEWS_TYPE: Int = 1
        const val SOURCE_TYPE: Int = 2
    }
}
