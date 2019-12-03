package com.weikappinc.weikapp.components
import android.os.Handler

private const val TAG = "Chronometer"

class Chronometer(private val listener: ChronometherListener, private val messageOnStop: String){

    private var isRunning = false

    private val handler = Handler()

    private var minutes = 0
    private var seconds  = 0
    private var currentTime = "0:00"

    private val periodicUpdate: Runnable = run {
        Runnable {
            if(isRunning) handler.postDelayed(periodicUpdate, 1 * 1000)

            listener.onTimeChange(currentTime)

            seconds++

            if(seconds == 60) {
                minutes++
                seconds = 0
            }

            currentTime = "$minutes:${if(seconds < 10) "0$seconds" else seconds.toString()}"
        }
    }

    fun start() {
        if(!isRunning) {
            handler.post(periodicUpdate)

            isRunning = true
        }
    }

    fun pause() {
        if(isRunning) {
            handler.removeCallbacks(periodicUpdate)

            isRunning = false
        }
    }

    // Paramos el cronómetro cuando el usuario detiene la grabación del audio
    fun stop() {
        if(isRunning) {
            handler.removeCallbacks(periodicUpdate)

            isRunning = false

            listener.onTimeChange(messageOnStop)

            minutes = 0
            seconds  = 0
            currentTime = "0:00"
        }
    }
}

interface ChronometherListener {
    fun onTimeChange(time: String)
}