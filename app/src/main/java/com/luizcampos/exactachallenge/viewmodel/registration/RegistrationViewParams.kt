package com.luizcampos.exactachallenge.viewmodel.registration

data class RegistrationViewParams(
    val name: String = "",
    val debtDate: String = "",
    val amount: String = "", // centavos
    val tags: String = "",
    val description: String = ""
)
