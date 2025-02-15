package com.reviling.filamentandroid.ui

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.ui.home.HomeViewModel
import com.reviling.filamentandroid.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseActivity : AppCompatActivity() {

    private var job: Job? = null

    abstract fun repeatFunction()

    private fun startRepeatingTask(interval: Long = 5000) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                repeatFunction() // Call the function defined in each activity
                delay(interval) // Wait for specified time (default 5 seconds)
            }
        }
    }

    private fun stopRepeatingTask() {
        job?.cancel() // Cancel the loop
    }

    override fun onResume() {
        super.onResume()
        startRepeatingTask() // Start the repeating task when the activity is visible
    }

    override fun onPause() {
        super.onPause()
        stopRepeatingTask() // Stop the task when the activity is paused
    }
}
