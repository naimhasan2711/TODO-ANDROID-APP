package com.nakibul.todo.service

import android.app.ActivityManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nakibul.todo.data.repository.TodoRepository
import com.nakibul.todo.utils.Constants
import com.nakibul.todo.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationJobService : JobService() {
    @Inject
    lateinit var todoRepository: TodoRepository
    lateinit var job: Job
    lateinit var serviceIntent: Intent


    override fun onStartJob(params: JobParameters): Boolean {
        val jsonItem = params.extras.get(Constants.TODO_ITEM_KEY) as String
        work(jsonItem)
        return true
    }


    private fun work(todo: String) {
        serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra(Constants.TODO_ITEM_KEY, todo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        job = CoroutineScope(Dispatchers.Default).launch {
            delay(Constants.DELAY_TIME_FIVE)
            stopService(serviceIntent)
            todoRepository.delete(Converters.jsonToGson(todo))
            SendBroadcast.sendUpdateBroadcast(
                applicationContext,
            )
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}