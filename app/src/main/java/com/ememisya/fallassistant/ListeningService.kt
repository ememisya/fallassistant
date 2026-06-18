package com.ememisya.fallassistant

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ememisya.fallassistant.manager.PreferencesManager
import com.ememisya.fallassistant.manager.SpeechRecognitionManager
import com.ememisya.fallassistant.util.NotificationUtil
import com.ememisya.fallassistant.util.TelephonyUtil

/**
 * Foreground service acting as the central coordination point.
 */
class ListeningService : Service() {

    companion object {
        var isRunning: Boolean = false
        private const val WAKE_COOLDOWN_MS = 3000L
        private const val CALL_COOLDOWN_MS = 5000L
    }

    private lateinit var prefsManager: PreferencesManager
    private var speechManager: SpeechRecognitionManager? = null

    private var lastCallTime = 0L
    private var lastWakeTime = 0L

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)

        if (!prefsManager.isVoskEnabled()) return

        isRunning = true

        NotificationUtil.createNotificationChannel(this)
        val notification = NotificationUtil.buildListeningNotification(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)
        }
        speechManager = SpeechRecognitionManager(this,
            prefsManager.getWakeWord()) {
            processWakeWord()
        }
        speechManager?.startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        speechManager?.stopListening()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /** Evaluates cooldowns before triggering the emergency protocol. */
    private fun processWakeWord() {
        val now = System.currentTimeMillis()
        if (now - lastWakeTime < WAKE_COOLDOWN_MS) return
        lastWakeTime = now

        executeEmergencyCall()
    }

    /** Triggers the dialing sequence or alerts the user if missing data. */
    private fun executeEmergencyCall() {
        val now = System.currentTimeMillis()
        if (now - lastCallTime < CALL_COOLDOWN_MS) return
        lastCallTime = now

        val number = prefsManager.getEmergencyNumber()
        if (number.isNullOrBlank()) {
            NotificationUtil.showMissingNumberAlert(this)
            return
        }

        TelephonyUtil.dialNumber(this, number)
    }
}