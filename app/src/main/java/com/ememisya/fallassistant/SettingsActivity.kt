package com.ememisya.fallassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.ComponentActivity
import com.ememisya.fallassistant.manager.PreferencesManager

/**
 * User interface for configuring application parameters.
 */
class SettingsActivity : ComponentActivity() {

    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefsManager = PreferencesManager(this)

        val numberField = findViewById<EditText>(R.id.numberField)
        val wakeWordField = findViewById<EditText>(R.id.wakeWordField)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val switchVosk = findViewById<Switch>(R.id.switch_vosk)

        loadCurrentSettings(numberField, wakeWordField, switchVosk)

        saveButton.setOnClickListener {
            saveSettings(numberField.text.toString(), wakeWordField.text.toString())
            restartListeningService()
            finish()
        }

        switchVosk.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setVoskEnabled(isChecked)
        }
    }

    /** Populates the UI with existing preferences. */
    private fun loadCurrentSettings(numberField: EditText, wakeWordField: EditText, switchVosk: Switch) {
        numberField.setText(prefsManager.getEmergencyNumber())
        wakeWordField.setText(prefsManager.getWakeWord())
        switchVosk.isChecked = prefsManager.isVoskEnabled()
    }

    /** Saves user input to preferences. */
    private fun saveSettings(number: String, wakeWord: String) {
        prefsManager.setEmergencyNumber(number)
        prefsManager.setWakeWord(wakeWord)
    }

    /** Restarts the background listener to apply changes immediately. */
    private fun restartListeningService() {
        stopService(Intent(this, ListeningService::class.java))
        startService(Intent(this, ListeningService::class.java))
    }
}