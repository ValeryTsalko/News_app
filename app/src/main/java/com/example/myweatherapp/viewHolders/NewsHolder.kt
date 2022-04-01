package com.example.myweatherapp.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.NewsHolderBinding
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.models.NewsModel
import java.time.format.DateTimeFormatter


class NewsHolder(private val newsListener: OnItemClickListener, private val sourceListener: OnSourceClickListener, itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = NewsHolderBinding.bind(itemView)

    fun bind(news: NewsModel, favoriteUrls: List<String>) = with(binding) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        newsTitle.text = news.newsTitle
        newDescription.text = news.newsDescription
        newsAuthor.text = news.newsAuthor
        newsDate.text = dateTimeFormatter.format(news.newsPublishedAt)
        Glide
            .with(binding.root.context)
            .load(news.newsUrlToImage)
            .thumbnail(0.1f)
            .centerInside()
            .into(newsImage)

        favoriteIcon.isChecked = favoriteUrls.contains(news.newsUrl)

        favoriteIcon.setOnClickListener {

            newsListener.onFavoriteIconClick(news.newsUrl)
        }

        root.setOnClickListener {
            sourceListener.onSourceClickListener(news.newsUrl)
        }

    }
}
