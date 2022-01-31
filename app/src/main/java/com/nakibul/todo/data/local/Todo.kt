package com.nakibul.todo.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey val id: String,
    val time: Long,
    val title: String,
    val todo: List<String>
) : Parcelable