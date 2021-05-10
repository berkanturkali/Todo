package com.example.todo.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.todo.BuildConfig
import com.example.todo.R


fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load("${BuildConfig.BASE_URL}/$url")
        .error(R.drawable.ic_placeholder)
        .into(this)
}

