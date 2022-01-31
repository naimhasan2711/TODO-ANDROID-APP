package com.nakibul.todo.data.repository

import androidx.lifecycle.LiveData

import com.nakibul.todo.data.local.TodoDao
import com.nakibul.todo.data.local.Todo
import com.nakibul.todo.data.remote.ApiInterface
import com.nakibul.todo.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class TodoRepository @Inject constructor(
    private val apiService: ApiInterface,
    private val todoDAO: TodoDao
) {

    //get data from remote source and insert into local DB
    suspend fun todoList() {
        val remoteResults = apiService.todoRemoteList()
        for (result in remoteResults) {
            val timeInMilli = SimpleDateFormat(Constants.TIME_DATE_FORMAT).parse(result.time).time
            if (timeInMilli >= Calendar.getInstance().timeInMillis) {
                todoDAO.insertData(
                    Todo(
                        result.id,
                        timeInMilli,
                        result.title,
                        result.todo
                    )
                )
            }

        }
    }

    //get data from local database
    fun getLocalData(): LiveData<List<Todo>> {
        return todoDAO.getData()
    }

    //get data from top of the local database table
    fun getLatestTodo(): Todo {
        return todoDAO.getLatestTodo()
    }

    //insert data into local database
    fun insertData(todo: Todo) {
        todoDAO.insertData(todo)
    }

    //delete data from local database
    fun delete(todo: Todo) {
        todoDAO.delete(todo)
    }

    //update data from local database
    fun update(todo: Todo) {
        todoDAO.update(todo)
    }

}