package com.luizcampos.exactachallenge.helper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luizcampos.exactachallenge.model.Tags
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {

        private val dateFormat: DateFormat
            get() = SimpleDateFormat("d MMM yyyy h:mm a", Locale("pt", "BR"))

        fun dumpStackTrace(listElement: Array<StackTraceElement>): String {
            var dumped = ""
            listElement.forEach { ste ->
                dumped += "\tat " + ste.className + "." + ste.methodName +
                        "(" + ste.fileName + ":" + ste.lineNumber + ")\n"
            }

            return dumped
        }

        fun tagsToGson(list: List<Tags>): String {
            return Gson().toJson(list)
        }

        fun gsonToTags(value: String): List<Tags> {
            val listType = object : TypeToken<List<Tags>>() {}.type
            return Gson().fromJson(value, listType)
        }

        fun getDate(timemillis: Long) : String {
            return dateFormat.format(Date(timemillis))
        }
    }
}