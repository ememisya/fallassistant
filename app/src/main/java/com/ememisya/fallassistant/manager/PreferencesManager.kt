package com.ememisya.fallassistant.manager

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages shared preferences for the application.
 */
class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fall_assistant", Context.MODE_PRIVATE)

    /** Retrieves the saved emergency number. */
    fun getEmergencyNumber(): String? = prefs.getString("emergency_number", null)

    /** Saves the emergency number. */
    fun setEmergencyNumber(number: String) = prefs.edit().putString("emergency_number", number).apply()

    /** Retrieves the wake phrase, defaulting to help me. */
    fun getWakeWord(): String = prefs.getString("wake_word", "help me") ?: "help me"

    /** Saves the custom wake phrase. */
    fun setWakeWord(word: String) = prefs.edit().putString("wake_word", word).apply()

    /** Checks if the Vosk listening service is enabled. */
    fun isVoskEnabled(): Boolean = prefs.getBoolean("vosk_enabled", false)

    /** Toggles the Vosk listening service state. */
    fun setVoskEnabled(enabled: Boolean) = prefs.edit().putBoolean("vosk_enabled", enabled).apply()
}