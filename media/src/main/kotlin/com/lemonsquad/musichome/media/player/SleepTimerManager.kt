package com.lemonsquad.musichome.media.player

import android.os.Handler
import android.os.Looper
import android.util.Log

class SleepTimerManager(private val onTimerExpired: () -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    
    var isRunning = false
        private set
        
    var remainingTimeMs: Long = 0
        private set

    fun start(durationMs: Long) {
        stop()
        isRunning = true
        remainingTimeMs = durationMs
        
        runnable = object : Runnable {
            override fun run() {
                if (remainingTimeMs <= 0) {
                    isRunning = false
                    onTimerExpired()
                } else {
                    remainingTimeMs -= 1000
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(runnable!!, 1000)
        Log.d("SleepTimer", "Timer started for $durationMs ms")
    }

    fun stop() {
        runnable?.let { handler.removeCallbacks(it) }
        runnable = null
        isRunning = false
        remainingTimeMs = 0
    }
}
