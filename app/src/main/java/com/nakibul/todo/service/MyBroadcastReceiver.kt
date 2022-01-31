package com.nakibul.todo.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import com.nakibul.todo.data.repository.TodoRepository
import com.nakibul.todo.utils.Constants
import com.nakibul.todo.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.Default).launch {
            stopJob(context)
            val todo = todoRepository.getLatestTodo()
            if (todo != null) {
                val timeGap = todo.time - Calendar.getInstance().time.time
                startJob(context, timeGap - Constants.DELAY_TIME_FIVE, Converters.gsonToJson(todo))
            }
        }
    }

    private fun stopJob(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(100)
    }

    private fun startJob(context: Context, time: Long, jsonItem: String) {
        Timber.d("Job will start after ${time / (60 * 1000)} min ${time / 1000} sec")
        val componentName = ComponentName(context, NotificationJobService::class.java)
        val persistentBundle = PersistableBundle()
        persistentBundle.putString(Constants.TODO_ITEM_KEY, jsonItem)
        val jobInfo = JobInfo.Builder(100, componentName)
            .setMinimumLatency(time)
            .setExtras(persistentBundle)
            .setPersisted(true)
            .build()
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
            Timber.d("Notification job scheduled for $jsonItem")
        }
    }

}