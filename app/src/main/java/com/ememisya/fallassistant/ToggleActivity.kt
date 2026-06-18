package com.ememisya.fallassistant

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.ememisya.fallassistant.manager.PreferencesManager

/**
 * Entry point for fast settings access and Assistant designation.
 */
class ToggleActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()

        if (intent?.action == "com.ememisya.fallassistant.SETTINGS") {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
            return
        }

        if (!isDefaultAssistant()) {
            openDefaultAssistantSettings()
            return
        }

        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, AssistantActivity::class.java))
            finish()
            return
        }
        val status = getListeningStatus()
        startForegroundService(Intent(this, ListeningService::class.java))
        Toast.makeText(this, "Started service. $status", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getListeningStatus(): String {
        val enabled = PreferencesManager(this).isVoskEnabled()
        val status = if (enabled) "Listening ENABLED." else "Listening DISABLED"
        return status
    }

    /** Validates if the application is currently assigned as the default system assistant. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun isDefaultAssistant(): Boolean {
        val roleManager = getSystemService(RoleManager::class.java)
        return roleManager.isRoleHeld(RoleManager.ROLE_ASSISTANT)
    }

    /** Routes the user to device settings to authorize the app as the primary assistant. */
    private fun openDefaultAssistantSettings() {
        try {
            startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
            Toast.makeText(this, "Please select FallAssistant as your Default Assistant App", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }
}