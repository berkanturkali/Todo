package com.example.todo.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.todo.BuildConfig
import com.example.todo.R

class GlideUtil {
    companion object{
        fun loadImage(context: Context, url:String, imageView: ImageView){
            Glide.with(context)
                .load("${BuildConfig.BASE_URL}/$url")
                .error(R.drawable.ic_person)
                .into(imageView)
        }
    }
}