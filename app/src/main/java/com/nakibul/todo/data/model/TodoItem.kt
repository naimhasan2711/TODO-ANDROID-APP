package com.nakibul.todo.data.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class TodoItem(
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("todo")
    val todo: List<String>
)