package com.nakibul.todo.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nakibul.todo.data.local.Todo
import java.text.SimpleDateFormat
import java.util.*

object Converters {
    fun timeToString(time: Long): String {
        val dateFormat = SimpleDateFormat(Constants.TIME_DATE_FORMAT)
        return dateFormat.format(Date(time))
    }

    fun jsonToGson(json: String): Todo {
        val type = object : TypeToken<Todo>() {}.type
        return Gson().fromJson(json, type)
    }

    fun gsonToJson(todoItem: Todo): String {
        val type = object : TypeToken<Todo>() {}.type
        return Gson().toJson(todoItem, type)
    }
}