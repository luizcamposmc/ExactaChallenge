package com.luizcampos.exactachallenge.repository

interface ToastRepository {
    fun error(message: String, duration: Int)

    fun success(message: String, duration: Int)

    fun info(message: String, duration: Int)

    fun warning(message: String, duration: Int)
}