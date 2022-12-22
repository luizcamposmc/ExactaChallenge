package com.luizcampos.exactachallenge.helper

sealed class FlowEvent {
    class Success<T>(val value: T) : FlowEvent()
    class Error(val message: String) : FlowEvent()
    object Loading : FlowEvent()
    object Empty : FlowEvent()
}
