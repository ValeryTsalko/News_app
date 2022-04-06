package com.example.myweatherapp

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

internal fun View.show() { visibility = View.VISIBLE }
internal fun View.hide() { visibility = View.GONE }

internal fun View.forceHideKeyboard() {
    context
        .getSystemService<InputMethodManager>()
        ?.hideSoftInputFromWindow(windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
}
