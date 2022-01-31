package com.nakibul.todo.ui.viewmodel

import androidx.lifecycle.*
import com.nakibul.todo.data.local.Todo

import com.nakibul.todo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel(), LifecycleObserver {

    //get local data
    val todoListLocal: LiveData<List<Todo>> get() = repository.getLocalData()

    //insert remote data into local database
    fun insertApiDataToLocalDb() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.todoList()
        }
    }

    //insert into local database
    fun insertNewTodo(todoListItem: Todo) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertData(todoListItem)
        }
    }

    //update into local database
    fun updateTask(todoItem: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todoItem)
        }
    }

    //update into local database
    fun deleteTask(todoItem: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(todoItem)
        }
    }

}