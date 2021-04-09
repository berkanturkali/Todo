package com.example.todo.util

import io.reactivex.rxjava3.disposables.Disposable

interface MyDisposable {
    fun safeAdd(disposable: Disposable)
    fun safeDispose()
}