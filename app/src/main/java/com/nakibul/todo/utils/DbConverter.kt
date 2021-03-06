package com.nakibul.todo.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

@ProvidedTypeConverter
class DbConverter {
    @TypeConverter
    fun toTodoItem(todoItem: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(todoItem, type)
    }

    @TypeConverter
    fun toTodoItemJson(todoItem: List<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(todoItem, type)
    }
}