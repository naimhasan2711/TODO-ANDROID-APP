package com.nakibul.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nakibul.todo.utils.DbConverter

@Database(entities = [Todo::class], version = 2)
@TypeConverters(DbConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TodoDao
}