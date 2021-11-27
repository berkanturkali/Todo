package com.example.todo.util


import android.widget.EditText
import android.widget.TextView
import androidx.core.util.PatternsCompat
import com.google.android.material.textfield.TextInputEditText

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotEmpty() && this.length > 6
}

fun String.isValidFirstNameOrLastName(): Boolean {
    val regexString = "^[a-zA-ZğüşöçİĞÜŞÖÇ]*\$"
    return isNotEmpty() && matches(regexString.toRegex())
}

fun TextInputEditText.text(): String = text.toString()

fun TextInputEditText.trim():String = text.toString().trim()

fun TextInputEditText.isValid(): Boolean {
    var isValid = false
    type()?.let { type ->
        isValid = when (type) {
            InputType.EMAIL -> {
                text().isValidEmail()
            }
            InputType.PASSWORD -> {
                text().isValidPassword()
            }
            InputType.NAME -> {
                text().isValidFirstNameOrLastName()
            }
        }
        if (!isValid) setError(type.message)
    }
    return isValid
}

fun TextInputEditText.capitalizeAndTrim() = text().replaceFirstChar(Char::titlecase).trim()

fun TextView.text() = text.toString()

fun TextInputEditText.type(): InputType? {
    return when (inputType) {
        (android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS.or(android.text.InputType.TYPE_CLASS_TEXT)) -> {
            InputType.EMAIL
        }
        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD.or(android.text.InputType.TYPE_CLASS_TEXT)
        -> {
            InputType.PASSWORD
        }
        android.text.InputType.TYPE_CLASS_TEXT -> {
            InputType.NAME
        }
        else -> null
    }
}

enum class InputType(val message: String) {
    EMAIL("Please provide a valid email."),
    NAME("Please provide a valid name."),
    PASSWORD("Password length must be greater than 6 characters.")
}