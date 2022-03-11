package com.example.myweatherapp.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.NewsHolderBinding
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.models.NewsModel


class NewsHolder(private val newsListener: OnItemClickListener, itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = NewsHolderBinding.bind(itemView)

    fun bind(news: NewsModel) = with(binding) {
        newsTitle.text = news.newsTitle
        newDescription.text = news.newsDescription
        newsAuthor.text = news.newsAuthor
        newsDate.text = news.newsPublishedAt
        Glide
            .with(binding.root.context)
            .load(news.newsUrlToImage)
            .thumbnail(0.1f)
            .centerInside()
            /*.override(400, 200)*/
            .into(newsImage)


        favoriteIcon.setOnClickListener {

            it.isSelected = !it.isSelected

            if (it.isSelected) {
                favoriteIcon.setImageResource(R.drawable.favorites_icon_filled)

            } else {
                favoriteIcon.setImageResource(R.drawable.favorites_icon_unfilled)
            }

            newsListener.onIconClickListener(news.newsUrl)  // блягодаря тому, что мы имплементим interface у Holdera и Activity мы можем передать
                                                        //  данные при клике из Holdera в Activity (небольшие данные), а передаем мы url нашей новости
        }

//            if (favoriteIcon.isSelected )  { // ???
//                favoriteIcon.setImageResource(R.drawable.favorites_icon_filled)
//            } else {
//                favoriteIcon.setImageResource(R.drawable.favorites_icon_unfilled)
//            }

    }
}