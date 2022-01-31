package com.nakibul.todo.di

import android.content.Context
import androidx.room.Room
import com.nakibul.todo.data.local.TodoDatabase
import com.nakibul.todo.utils.Constants
import com.nakibul.todo.utils.DbConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDB(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        TodoDatabase::class.java,
        Constants.DATABSE_NAME
    )
        .fallbackToDestructiveMigration()
        .addTypeConverter(DbConverter())
        .build()

    @Singleton
    @Provides
    fun provideTodoDAO(db: TodoDatabase) = db.taskDao()
}