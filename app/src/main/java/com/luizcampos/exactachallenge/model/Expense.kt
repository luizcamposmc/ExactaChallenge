package com.luizcampos.exactachallenge.model

data class Expense(
    val id: String,
    val name: String,
    val debtDate: String,
    val amount: String, // centavos
    val tags: String,
    val description: String
)
