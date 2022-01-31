package com.nakibul.todo.di

import com.nakibul.todo.data.local.TodoDao
import com.nakibul.todo.data.remote.ApiInterface
import com.nakibul.todo.data.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides

    fun provideTodoRepository(apiInterface: ApiInterface, todoDao: TodoDao): TodoRepository {
        return TodoRepository(apiInterface, todoDao)
    }
}