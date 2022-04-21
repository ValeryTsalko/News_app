package com.example.myweatherapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.repository.NewsRepository
import com.example.myweatherapp.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClickListener, OnSourceClickListener {

    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val repository = NewsRepository()
    private val newsAdapter = NewsAdapter(this, this)
    private val searchAdapter by lazy {
        ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mutableListOf()).apply {
            setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = MainViewModel(application)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel.getProgressVisibility.observe(this) { isProgressBarVisible ->
            binding.progressBar.progressBar.isVisible = isProgressBarVisible
        }

        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mViewModel.getNewsData.observe(this) { data ->
            newsAdapter.setData(data)
        }

        mViewModel.loadNewsData()


        mViewModel.getSourceData.observe(this) { data ->
            newsAdapter.setData(data)
        }

        mViewModel.getFavoriteData.observe(this) { data ->
            newsAdapter.setData(data)
        }

        mViewModel.getSpinnerItems.observe(this) { data ->
            searchAdapter.addAll(data)
        }
        mViewModel.loadSpinnerData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.searchIcon.show()
        binding.toolbar.searchNews.show()

        init()

        val spinner: Spinner = findViewById(R.id.search_icon)
        spinner.adapter = searchAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val source = searchAdapter.getItem(position)
                if (source != "all sources") {
                    binding.progressBar.root.show()
                    repository.getNews(source) { list ->
                        if (list != null) {
                            newsAdapter.setData(list)
                            Log.d("TAG", "GETTING SORTED SOURCE DATA")
                            binding.progressBar.root.hide()
                        } else {
                            Log.d("TAG", "Some ERROR")
                        }
                    }
                } else {
                    mViewModel.loadNewsData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.bottomNV.selectedItemId = R.id.item1
        binding.toolbar.toolName.text = resources.getString(R.string.tool_bar_news)
        binding.editText.editText.hide()

        binding.editText.editText.setOnEditorActionListener { v, actionId, event ->

            if (actionId == IME_ACTION_SEARCH) {
                binding.editText.editText.hide()
            }
            false
        }

        binding.toolbar.searchNews.setOnClickListener {

            binding.editText.editText.show()

            /**
             * left this for later
             * this object might be added to the textWatcher
             */
            /*   binding.editText.editText.setOnKeyListener(object : View.OnKeyListener { //
                   override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                       if (event != null) {
                           if (event.action == KeyEvent.ACTION_DOWN &&
                               keyCode == KeyEvent.KEYCODE_ENTER
                           ) {
                               return true
                           }
                       }
                       return false
                   }
               })*/
        }

        binding.editText.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if (!s.isNullOrEmpty() && s.toString().length >= 3) {
                                repository.getNews(s.toString()) { list ->
                                    if (list != null) {
                                        mViewModel.applyIsFavoriteState(list)
                                        newsAdapter.setData(list)
                                        binding.editText.editText.hide()
                                        binding.editText.editText.forceHideKeyboard()
                                        binding.editText.editText.clearFocus()
                                        binding.editText.editText.isCursorVisible = false
                                        Log.d("tag ", "Getting Sorted News by param")
                                    }
                                }
                            } else {
                                binding.editText.editText.hide()
                                mViewModel.loadNewsData()
                            }
                        }
                    }, delay
                    )
                } catch (ex: Exception) {
                    Log.d("ERROR", "${ex.message}")
                    ex.printStackTrace()
                }
            }

            private var timer: Timer = Timer()
            private val delay = 1000L

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.bottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item1 -> {
                    with(binding) {
                        editText.editText.hide()
                        toolbar.searchNews.show()
                        toolbar.searchIcon.show()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_news)
                    }

                    mViewModel.loadNewsData()
                    true
                }
                R.id.item2 -> {
                    with(binding) {
                        editText.editText.hide()
                        toolbar.searchNews.hide()
                        toolbar.searchIcon.hide()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_source)
                    }

                    mViewModel.loadSourceData()
                    true
                }
                R.id.item3 -> {
                    with(binding) {
                        editText.editText.hide()
                        toolbar.searchNews.hide()
                        toolbar.searchIcon.hide()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_favorite)
                    }
                    searchAdapter.clear()
                    mViewModel.loadFavoriteData()
                    true
                }
                else -> false
            }
        }
    }

    override fun onIconClick(url: String) {
        mViewModel.addNewsToFavoriteList(url)
    }

    override fun onFavoriteIconClick(url: String) {
        when (binding.bottomNV.selectedItemId) {
            R.id.item1 -> {
                onIconClick(url)
            }

            R.id.item3 -> {
                mViewModel.deleteNewsFromFavoriteList(url)
            }
        }
    }

    override fun onSourceClickListener(url: String) {

        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)

        intent.data = Uri.parse(url)

        try {
            startActivity(intent)
            Log.d("Tag", "Opening source news")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "Browser not wound on current device", Toast.LENGTH_SHORT).show()
            ex.printStackTrace()

            val intentWeb = Intent(this, WebViewActivity::class.java) // для работы необходим установленный браузер
            intentWeb.putExtra("url", url)
            startActivity(intentWeb)
        }
    }

    private fun init() {
        binding.apply {
            recyclerViewNews.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerViewNews.adapter = newsAdapter
            Log.d("TAG", "ADAPTER STARTED")
        }
    }
}
