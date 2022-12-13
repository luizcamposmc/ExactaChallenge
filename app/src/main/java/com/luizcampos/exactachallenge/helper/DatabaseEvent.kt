package com.luizcampos.exactachallenge.helper

sealed class DatabaseEvent {
    class Success<T>(val value: T) : DatabaseEvent()
    class Error(val message: String) : DatabaseEvent()
    object Loading : DatabaseEvent()
    object Empty : DatabaseEvent()
}
