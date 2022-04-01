package com.example.myweatherapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.adapters.NewsAdapter
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.interfaces.IListItem
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.models.NewsModel
import com.example.myweatherapp.repository.NewsRepository
import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity(), OnItemClickListener, OnSourceClickListener {

    private var sharedPreferences: SharedPreferences? = null
    private lateinit var binding: ActivityMainBinding
    private val newsAdapter = NewsAdapter(this, this)
    private  val repository = NewsRepository()


    private val searchAdapter by lazy {
        ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mutableListOf()).apply {
            setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchAdapter.add("all sources")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.root.show()
        sharedPreferences = getSharedPreferences("Url", MODE_PRIVATE)

        loadNewsData()

        init()

        binding.toolbar.searchNews.show()
        binding.toolbar.searchIcon.show()
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
                    loadNewsData()
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

        binding.toolbar.searchNews?.setOnClickListener {

            binding.editText.editText.show()

            binding.editText.editText.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event != null) {
                        if (event.action == KeyEvent.ACTION_DOWN &&
                            keyCode == KeyEvent.KEYCODE_ENTER
                        ) {
                            binding.editText.editText.text = binding.editText.editText.text
                            binding.editText.editText.clearFocus()
                            binding.editText.editText.isCursorVisible = false
                            return true
                        }
                    }
                    return false
                }
            })
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
                                        newsAdapter.updateUrlList(getFavoriteUrlList())
                                        newsAdapter.setData(list)
                                        binding.editText.editText.hide()
                                        Log.d("tag ", "Getting Sorted News by param")
                                    }
                                }
                            } else {
                                binding.editText.editText.hide()
                                loadNewsData()
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
                    with (binding) {
                        editText.editText.hide()
                        toolbar.searchNews.show()
                        toolbar.searchIcon.show()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_news)
                    }

                    loadNewsData()
                    true
                }
                R.id.item2 -> {
                    with (binding) {
                        editText.editText.hide()
                        toolbar.searchNews.hide()
                        toolbar.searchIcon.hide()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_source)
                    }

                    loadSourceData()
                    true
                }
                R.id.item3 -> {
                    with (binding) {
                        editText.editText.hide()
                        toolbar.searchNews.hide()
                        toolbar.searchIcon.hide()
                        toolbar.toolName.text = resources.getString(R.string.tool_bar_favorite)
                    }

                    loadFavoriteList()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadNewsData() {
        runOnUiThread {
            binding.progressBar.root.show()
        }
        return repository.getNews("news") { list ->
            if (list != null) {
                searchAdapter.clear()
                searchAdapter.add("all sources")
                val spinnerList = HashSet<String>()
                list.forEach {
                    if (it.newsSource?.name?.isNotEmpty()) {
                        spinnerList.add(it.newsSource?.name)
                    }
                }
                searchAdapter.addAll(spinnerList)
                newsAdapter.updateUrlList(getFavoriteUrlList())
                newsAdapter.setData(list)
                Log.d("TAG", "GETTING NEWS DATA")
                runOnUiThread {
                    binding.progressBar.root.hide()
                }
            } else {
                Log.d("TAG", "SOME ERROR")
            }
        }
    }

    private fun loadSourceData () {
        binding.progressBar.root.show()
        return repository.getSource { list ->
            if (list != null) {
                newsAdapter.setData(list)
                Log.d("TAG", "GETTING SOURCE DATA")
                binding.progressBar.root.hide()
            } else {
                Log.d("TAG", "Some ERROR")
            }
        }
    }

    private fun loadFavoriteList() {
        binding.progressBar.root.show()
        return repository.getNews("news") { list ->
            if (list != null) {
                val favoriteList = mutableListOf<NewsModel>()
                val keySet = sharedPreferences?.getStringSet(KEY_URL, emptySet()) ?: emptySet()

                keySet.forEach { url ->
                    list.find { it.newsUrl == url }?.let {
                        favoriteList.add(it)
                    }
                }
                newsAdapter.updateUrlList(getFavoriteUrlList())
                newsAdapter.setData(favoriteList)
                Log.d("TAG", "GETTING NEWS DATA")
                binding.progressBar.root.hide()
            } else {
                Log.d("TAG", "SOME ERROR")
            }
        }
    }

    private fun init() {
        binding.apply {
            recyclerViewNews.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerViewNews.adapter = newsAdapter
            Log.d("TAG", "ADAPTER STARTED")
        }
    }

    override fun onIconClick(url: String) {

        val keySet = sharedPreferences?.getStringSet(KEY_URL, emptySet())
        val mutableKeySet = mutableSetOf<String>()

        sharedPreferences?.edit()?.let { editor ->
            if (keySet.isNullOrEmpty()) {
                editor.putStringSet(KEY_URL, setOf(url))
            } else {
                mutableKeySet.apply {
                    addAll(keySet)
                    add(url)
                }
                editor.putStringSet(KEY_URL, mutableKeySet)
            }
        }?.apply()
    }

    override fun onFavoriteIconClick(url: String) {
        when (binding.bottomNV.selectedItemId) {
            R.id.item1 -> {
                onIconClick(url)
            }

            R.id.item3 -> {
                val favoriteKeyUrls = sharedPreferences?.getStringSet(KEY_URL, emptySet()) ?: emptySet()
                val mutableFavoriteNews = mutableSetOf<String>()

                sharedPreferences?.edit()?.let { editor ->
                    mutableFavoriteNews.apply {
                        addAll(favoriteKeyUrls)
                        remove(url)
                    }
                    sharedPreferences?.edit()?.putStringSet(KEY_URL, mutableFavoriteNews)
                }?.apply()

                val currentFavoriteUrlList = getFavoriteUrlList()
                val updatedFavoriteList = ArrayList<IListItem>()


                    repository.getNews("news") { list ->
                        if (list != null) {
                            list.forEach {
                                if (currentFavoriteUrlList.contains(it.newsUrl)) {
                                    updatedFavoriteList.add(it)
                                }
                            }
                            newsAdapter.updateUrlList(mutableFavoriteNews)
                            newsAdapter.setData(updatedFavoriteList)
                        } else {
                            Log.d("TAG", "Favorite list is empty")
                        }
                    }
            }
        }
    }

    companion object {
        const val KEY_URL = "KEY_URL"
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

    fun getFavoriteUrlList(): Set<String> {
        return sharedPreferences?.getStringSet(KEY_URL, emptySet()) ?: emptySet()
    }
}
