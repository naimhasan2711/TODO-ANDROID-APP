package com.nakibul.todo.service

import android.content.Context
import android.content.Intent
import com.nakibul.todo.utils.Constants
import timber.log.Timber

object SendBroadcast {
    fun sendUpdateBroadcast(context: Context) {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        Timber.d("Broadcast sent")
        intent.action = Constants.DATA_UPDATE_INTENT
        context.sendBroadcast(intent)
    }
}