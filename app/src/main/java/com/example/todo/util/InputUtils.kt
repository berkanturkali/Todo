package com.example.todo.util

import androidx.core.util.PatternsCompat

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotEmpty() && this.length > 6
}

fun String.isValidFirstNameOrLastName(): Boolean {
    val regexString = "^[a-zA-ZğüşöçİĞÜŞÖÇ]*\$"
    return matches(regexString.toRegex())
}