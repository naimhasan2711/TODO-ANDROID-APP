package com.nakibul.todo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_table ORDER by time ASC")
    fun getData(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table ORDER BY time ASC LIMIT 1")
    fun getLatestTodo(): Todo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(vararg todoItem: Todo)

    @Delete
    fun delete(todoItem: Todo)

    @Update
    fun update(todoItem: Todo)

    @Query("DELETE FROM todo_table")
    fun deleteAll()

}