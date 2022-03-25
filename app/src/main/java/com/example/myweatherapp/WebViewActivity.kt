package com.example.myweatherapp

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.myweatherapp.databinding.WebViewActivityBinding

class WebViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = WebViewActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        intent.getStringExtra("url")

    }
}
