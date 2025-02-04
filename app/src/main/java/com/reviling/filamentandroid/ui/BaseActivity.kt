package com.reviling.filamentandroid.ui

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {

    private var job: Job? = null

    // Override this function to provide specific functionality in each activity
    abstract fun repeatFunction()

    fun startRepeatingTask(interval: Long = 5000) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) { // Loop until canceled
                repeatFunction() // Call the function defined in each activity
                delay(interval) // Wait for specified time (default 5 seconds)
            }
        }
    }

    fun stopRepeatingTask() {
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
