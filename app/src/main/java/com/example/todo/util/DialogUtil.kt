package com.example.todo.util

import android.app.AlertDialog
import android.content.Context
import android.widget.TextView

class DialogUtil {
    companion object {
        fun showDialog(context: Context, title: String, array: Array<String>, textView: TextView) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setItems(array) { dialog, which ->
                textView.setText(array[which])
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}