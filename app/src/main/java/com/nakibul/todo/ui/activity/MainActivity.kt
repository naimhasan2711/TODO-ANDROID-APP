package com.nakibul.todo.ui.activity

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.nakibul.todo.R
import com.nakibul.todo.databinding.ActivityMainBinding
import com.nakibul.todo.service.MyBroadcastReceiver
import com.nakibul.todo.service.SyncJobService
import com.nakibul.todo.utils.ConnectivityReceiver
import com.nakibul.todo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private var snackBar: Snackbar? = null
    private val broadcastReceiver by lazy {
        MyBroadcastReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        val navController = findNavController(R.id.fragment)
        val config = AppBarConfiguration(navController.graph)
        binding.toolBar.setupWithNavController(navController, config)
        setupActionBarWithNavController(navController)
        registerReceiver()
        handleSharedPreference()
        darkMode()
        syncFirstTime()
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    private fun registerReceiver() {
        val intentFilter = IntentFilter(Constants.DATA_UPDATE_INTENT)
        registerReceiver(broadcastReceiver, intentFilter)
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment)
        return when (item.itemId) {
            R.id.menu_sync -> {
                sync()
                true
            }
            else -> item.onNavDestinationSelected(navController) || onOptionsItemSelected(item)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            makeSnackBar(getString(R.string.offline), Color.RED)
        } else {
            makeSnackBar(getString(R.string.connected), Color.GREEN)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeSnackBar(message: String, color: Int) {
        snackBar = Snackbar.make(findViewById(R.id.rootLayout), message, 2000)
        val snackBarView = snackBar!!.view
        snackBarView.setBackgroundColor(color)
        snackBar!!.show()
    }

    private fun darkMode() {
        when (sharedPreferences.getString("theme", Constants.FOLLOW_SYSTEM)) {
            Constants.DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Constants.LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Constants.FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun handleSyncJob() {
        val syncOnOff = sharedPreferences.getBoolean("sync", false)
        if (!syncOnOff) {
            val jobScheduler =
                application.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(Constants.SYNC_JOB_ID)
        } else {
            sync()
        }
    }

    private fun handleSharedPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferenceChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String? ->
                when (key) {
                    "sync" -> {
                        handleSyncJob()
                    }
                    "period" -> {
                        sync()
                    }
                    "theme" -> {
                        darkMode()
                    }
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun syncFirstTime() {
        val firstLaunch = sharedPreferences.getBoolean(Constants.FIRST_LAUNCH_KEY, true)

        if (firstLaunch) {
            sharedPreferences.edit().putBoolean(Constants.FIRST_LAUNCH_KEY, false).apply()
            sync()
        }
    }

    private fun sync() {
        val jobScheduler =
            application.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(Constants.SYNC_JOB_ID)
        val hours = sharedPreferences.getString("period", "6")!!.toLong()
        val componentName = ComponentName(applicationContext, SyncJobService::class.java)
        val jobInfo = JobInfo.Builder(Constants.SYNC_JOB_ID, componentName)
            .setPeriodic(hours * 60 * 60 * 1000)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
        val result = jobScheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
            Timber.d("Sync job scheduled with $hours period")
        }
    }

}