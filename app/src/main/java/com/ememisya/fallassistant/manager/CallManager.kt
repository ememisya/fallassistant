package com.ememisya.fallassistant.manager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

/**
 * Manages phone calls, telephony states, and audio routing.
 *
 * @param context The application context.
 */
class CallManager(private val context: Context) {
    private var lastCallTime = 0L

    /**
     * Initiates an emergency phone call if the cooldown period has passed.
     *
     * @param number The phone number to dial.
     */
    fun dialEmergencyContact(number: String) {
        val now = System.currentTimeMillis()
        if (now - lastCallTime < 5000) return
        lastCallTime = now

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) return

        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("android.telecom.extra.START_CALL_WITH_SPEAKERPHONE", true)
        }

        Handler(Looper.getMainLooper()).post {
            context.startActivity(intent)
        }
    }
}