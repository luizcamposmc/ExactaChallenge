package com.luizcampos.exactachallenge.helper

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class MoneyTextWatcher (editText: EditText) : TextWatcher {
    companion object {
        private val numberFormat: NumberFormat =
            NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun parseCurrencyValue(value: String): BigDecimal {
            try {
                val replaceRegex: Regex = String.format(
                    "[%s,.\\s]",
                    Objects.requireNonNull(numberFormat.currency).displayName
                ).toRegex()
                val currencyValue: String = value.replace(replaceRegex, "")
                val cleared: String = currencyValue.replace("$", "")

                return BigDecimal(cleared).setScale(
                    2, BigDecimal.ROUND_FLOOR
                ).divide(
                    BigDecimal(100), BigDecimal.ROUND_FLOOR
                )
            } catch (ex: Exception) {
                Log.e(MoneyTextWatcher::class.java.simpleName, "Regex error!", ex)
            }

            return BigDecimal.ZERO
        }

        fun toFormat(value: String) : String {
            numberFormat.maximumFractionDigits = 2
            numberFormat.roundingMode = RoundingMode.FLOOR

            val parsed: BigDecimal = parseCurrencyValue(value)

            return numberFormat.format(parsed)
        }
    }

    private var editTextWeakReference: WeakReference<EditText> = WeakReference(editText)

    init {
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = RoundingMode.FLOOR
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        val et = editTextWeakReference.get()

        if (et == null || et.text.isNullOrBlank()) {
            return
        }

        et.removeTextChangedListener(this)

        val parsed: BigDecimal = parseCurrencyValue(et.text.toString())
        val formatted: String = numberFormat.format(parsed)

        et.setText(formatted)
        try {
            et.setSelection(formatted.length)
        } catch (indexOutOfBoundsException: IndexOutOfBoundsException) {
            et.setSelection(formatted.length - 1)
        }
        et.addTextChangedListener(this)
    }
}