package com.example.todo.util

import android.app.AlertDialog
import android.content.Context
import android.widget.TextView

fun Array<String>.showDialog(
    context: Context,
    title: String,
    textView: TextView
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setItems(this) { dialog, which ->
        textView.text = this[which]
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
}

