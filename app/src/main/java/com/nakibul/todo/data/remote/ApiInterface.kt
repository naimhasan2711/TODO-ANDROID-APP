package com.nakibul.todo.data.remote


import com.nakibul.todo.data.model.BaseTodo
import retrofit2.http.GET

interface ApiInterface {
    @GET(ApiUrls.TASK_URL)
    suspend fun todoRemoteList(): BaseTodo
}