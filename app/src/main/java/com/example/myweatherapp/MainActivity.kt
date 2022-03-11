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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.interfaces.OnItemClickListener
import com.example.myweatherapp.interfaces.OnSourceClickListener
import com.example.myweatherapp.models.NewsModel
import com.example.myweatherapp.repository.NewsRepository

class MainActivity : AppCompatActivity(), OnItemClickListener, OnSourceClickListener {


    private var sharedPreferences: SharedPreferences? = null  // обращаемся к списку сразу после создания и получаем краш

    private lateinit var binding: ActivityMainBinding
    private val newsAdapter = NewsAdapter(this, this)
    private val repository = NewsRepository()
    private val searchAdapter by lazy {
        ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mutableListOf()).apply {
            setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.progressBar.show()

        val newsRepository = NewsRepository()

        newsRepository.getNews { list ->
            if (list != null) {
                newsAdapter.setData(list)
                // получение данные с сервера
                Log.d("TAG", "Started")
                binding.progressBar.progressBar.hide()
            } else {
                Log.d("TAG", "Some Error")
            }
        }


        init()

        val spinner: Spinner = findViewById(R.id.search_icon)

        spinner.adapter = searchAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = searchAdapter.getItem(position)
                binding.progressBar.progressBar.show()// в переменную category записываем позицию типа int
                repository.getSource(category) { list ->
                    if (list != null) {
                        newsAdapter.setData(list)
                        Log.d("TAG", "GETTING SORTED SOURCE DATA")
                        binding.progressBar.progressBar.hide()
                    } else {
                        Log.d("TAG", "Some ERROR")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding.bottomNV.selectedItemId = R.id.item1

        binding.toolbar.toolName.text = resources.getString(R.string.tool_bar_news)

        binding.editText.editText.hide()

        binding.editText.editText.setOnEditorActionListener { v, actionId, event ->

            if (actionId == IME_ACTION_DONE) {
                binding.editText.editText.hide()

            }
            false
        }

        binding.toolbar.editTextIcon?.setOnClickListener {

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
                Log.d("tag", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.bottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item1 -> {
                    binding.toolbar.toolName.text = resources.getString(R.string.tool_bar_news)
                    loadNewsData()
                    true
                }
                R.id.item2 -> {
                    binding.toolbar.toolName.text = resources.getString(R.string.tool_bar_source)
                    loadSourceData()
                    true

                }
                R.id.item3 -> {
                    sharedPreferences = getSharedPreferences("Url", MODE_PRIVATE)
                    binding.toolbar.toolName.text = resources.getString(R.string.tool_bar_favorite)
                    loadFavoriteList()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadNewsData() {
        binding.progressBar.progressBar.show()
        return repository.getNews { list ->
            if (list != null) {
                newsAdapter.setData(list)
                Log.d("TAG", "GETTING NEWS DATA")
                binding.progressBar.progressBar.hide()
            } else {
                Log.d("TAG", "SOME ERROR")
            }
        }
    }

    private fun loadSourceData() {

        binding.progressBar.progressBar.show()

        return repository.getSource { list ->
            if (list != null) {

                val spinnerList = list.map { it.sourceCategory }.toSet()
                searchAdapter.clear()// не Set
                newsAdapter.setData(list)
                searchAdapter.addAll(spinnerList)

                Log.d("TAG", "GETTING SOURCE DATA")
                binding.progressBar.progressBar.hide()
            } else {
                Log.d("TAG", "Some ERROR")
            }
        }
    }

    private fun loadFavoriteList() {
        binding.progressBar.progressBar.show()
        return repository.getNews { list ->
            if (list != null) {
                val filteredList =
                    mutableListOf<NewsModel>()                                   //объявляем список отфильтрованные новостей
                val keySet = sharedPreferences?.getStringSet(KEY_URL, emptySet()) ?: emptySet()

                keySet.forEach { url ->
                    list.find { it.newsUrl == url }?.let { filteredList.add(it) }
                }

                newsAdapter.setData(filteredList)
                Log.d("TAG", "GETTING NEWS DATA")
                binding.progressBar.progressBar.hide()
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

    override fun onIconClickListener(url: String) {

        val keySet =
            sharedPreferences?.getStringSet(KEY_URL, emptySet()) // создаем объект SharedPreferences определяем переменную
        // которая будет хранить стринги URl-ов, emptySet - это дефотное значение
        val mutableKeySet = mutableSetOf<String>()

        Log.d("TAG", "$sharedPreferences")
        sharedPreferences?.edit()?.let { editor ->
            if (keySet.isNullOrEmpty()) {                               // проверка при запуске вкладки -> если список пуст
                editor.putStringSet(KEY_URL, setOf(url))
            } else {                                                    // если ->
                mutableKeySet.apply {
                    addAll(keySet)
                    add(url)
                }
                editor.putStringSet(KEY_URL, mutableKeySet)
            }
        }?.apply()
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

            /* val intentWeb = Intent(this, WebViewActivity::class.java)
             intentWeb.putExtra("url", url)
             startActivity(intentWeb)*/
        }
    }
}



