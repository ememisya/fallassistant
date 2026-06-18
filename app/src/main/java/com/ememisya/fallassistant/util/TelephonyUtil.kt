package com.ememisya.fallassistant.util


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Utility class handling phone calls and audio routing.
 */
object TelephonyUtil {

    const val DEFAULT_URI_SCHEME = "tel"

    /**
     * Dials the provided emergency number and forces speakerphone mode.
     *
     * @param number The phone number to dial.
     */
    fun dialNumber(context: Context, number: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TelephonyUtil", "Call permission not granted")
            return
        }

        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("$DEFAULT_URI_SCHEME:$number")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("android.telecom.extra.START_CALL_WITH_SPEAKERPHONE", true)
        }

        Handler(Looper.getMainLooper()).post {
            context.startActivity(intent)
            Log.d("TelephonyUtil", "Intent successfully sent to the system.")
            val telecom = context.getSystemService(TelecomManager::class.java)
            val accounts = telecom.callCapablePhoneAccounts

            Log.d("TelephonyUtil", "Accounts: $accounts")
            Log.d("TelephonyUtil", "Default outgoing: ${telecom.getDefaultOutgoingPhoneAccount(DEFAULT_URI_SCHEME)}")
        }
    }


}