package com.nakibul.todo.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import com.nakibul.todo.data.repository.TodoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SyncJobService:JobService() {
    @Inject
    lateinit var todoRepository: TodoRepository

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.d("Sync started")
        CoroutineScope(Dispatchers.Default).launch { todoRepository.todoList() }
            .invokeOnCompletion {
                SendBroadcast.sendUpdateBroadcast(
                    applicationContext
                )
            }
        Toast.makeText(applicationContext,"Sync completed",Toast.LENGTH_LONG).show()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.d("SyncJOB stopped")
        return true
    }
}