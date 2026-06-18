package com.ememisya.fallassistant

import android.content.Intent
import android.os.Build
import android.service.voice.VoiceInteractionService
import android.util.Log
import androidx.annotation.RequiresApi

class FallAssistantVoiceService : VoiceInteractionService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReady() {
        try {
            // Only execute super if the system manager binder actually exists
            super.onReady()
            val intent = Intent(this, ListeningService::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            this.startForegroundService(intent)
            Log.d("FallAssistant", "VoiceInteractionService successfully initialized!")
        } catch (e: NullPointerException) {
            Log.w("FallAssistant", "System Voice Interaction Manager is null. User must set this app as Default Assistant.")
        }
    }
}
