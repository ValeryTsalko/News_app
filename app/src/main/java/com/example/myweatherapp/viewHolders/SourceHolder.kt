package com.example.myweatherapp.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.databinding.SourceHolderBinding
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.models.SourceModel

class SourceHolder(
    private val sourceListener: OnSourceClickListener,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val binding = SourceHolderBinding.bind(itemView)

    fun bind(news: SourceModel) = with(binding) {
        sourceName.text = news.sourceName
        sourceCategory.text = news.sourceCategory
        sourceDescription.text = news.sourceDescription
        sourceCountry.text = news.sourceCountry

        root.setOnClickListener {
            sourceListener.onSourceClickListener(news.sourceUrl)
        }
    }
}