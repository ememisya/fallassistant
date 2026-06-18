package com.ememisya.fallassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

/**
 * Handles necessary application permissions upon first launch.
 */
class AssistantActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results.values.all { it }) startListeningService()
            finish()
        }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()
    }

    /** Evaluates and prompts for all required manifest permissions. */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun requestRequiredPermissions() {
        val requiredPermissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )

        val needed = requiredPermissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        } else {
            startListeningService()
            finish()
        }
    }

    /** Starts the continuous listening background service. */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startListeningService() {
        val intent = Intent(this, ListeningService::class.java)
        startForegroundService(intent)
    }
}