package com.example.myweatherapp.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.NewsHolderBinding
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.models.NewsModel
import java.time.format.DateTimeFormatter


class NewsHolder(private val newsListener: OnItemClickListener, itemView: View) : RecyclerView.ViewHolder(itemView) {

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

        if (favoriteUrls.contains(news.newsUrl)) {
            favoriteIcon.setImageResource(R.drawable.favorites_icon_filled)
        } else {
            favoriteIcon.setImageResource(R.drawable.favorites_icon_unfilled)
        }

        favoriteIcon.setOnClickListener {

            it.isSelected = !it.isSelected

            if (it.isSelected) {
                favoriteIcon.setImageResource(R.drawable.favorites_icon_filled)

            } else {
                favoriteIcon.setImageResource(R.drawable.favorites_icon_unfilled)
            }

            newsListener.onFavoriteIconClick(news.newsUrl)
             // блягодаря тому, что мы имплементим interface у Holdera и Activity мы можем передать
            //  данные при клике из Holdera в Activity (небольшие данные), а передаем мы url нашей новости
        }

    }
}