package com.luizcampos.exactachallenge.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.luizcampos.exactachallenge.helper.ToastDurationProvider
import es.dmoral.toasty.Toasty

class ToastyDataSource constructor(
    val context: Context,
    private val durationProvider: ToastDurationProvider
) : ToastRepository {
    override fun error(message: String, duration: Int) {
        show(Toasty.error(context, message, duration, true), duration)
    }

    override fun success(message: String, duration: Int) {
        show(Toasty.success(context, message, duration, true), duration)
    }

    override fun info(message: String, duration: Int) {
        show(Toasty.info(context, message, duration, true), duration)
    }

    override fun warning(message: String, duration: Int) {
        show(Toasty.warning(context, message, duration, true), duration)
    }

    private fun show(toast: Toast, duration: Int) {
        toast.show()

        if (
            duration != durationProvider.LENGTH_SHORT &&
            duration != durationProvider.LENGTH_LONG
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                toast.cancel()
            }, duration.toLong())
        }
    }
}