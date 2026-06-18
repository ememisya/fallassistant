package com.ememisya.fallassistant.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService

/**
 * Manages the Vosk speech recognition lifecycle and continuous audio capture.
 */
class SpeechRecognitionManager(
    private val context: Context,
    private val targetPhrase: String,
    private val onWakeWordDetected: () -> Unit
) {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var audioRecord: AudioRecord? = null
    private var wakeThread: Thread? = null
    private var isWakeLoopRunning = false

    /** Unpacks the Vosk model from assets and prepares audio. */
    fun startListening() {
        StorageService.unpack(context, "model-en", "model",
            { unpackedModel ->
                model = unpackedModel
                initAudioAndStartListening()
            },
            { exception -> Log.e("FallAssistant", "Failed to unpack model", exception) }
        )
    }

    /** Configures audio hardware and begins the recognition thread. */
    private fun initAudioAndStartListening() {
        recognizer = Recognizer(model, 16000.0f).apply {
            setMaxAlternatives(0)
            setWords(false)
        }

        val bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
            startWakeLoop()
        }
    }

    /** Loops continuously capturing audio and feeding it to Vosk. */
    private fun startWakeLoop() {
        if (isWakeLoopRunning) return
        isWakeLoopRunning = true

        wakeThread = Thread {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)
            audioRecord?.startRecording()
            val buffer = ByteArray(4096)

            while (isWakeLoopRunning) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0 && recognizer?.acceptWaveForm(buffer, read) == true) {
                    handleResult(recognizer!!.result)
                }
            }
            audioRecord?.stop()
        }.apply { start() }
    }

    /** Parses the JSON result from Vosk to find the wake phrase. */
    private fun handleResult(json: String) {
        try {
            val text = JSONObject(json).optString("text", "").lowercase()
            if (text.contains(targetPhrase.lowercase())) {
                onWakeWordDetected()
            }
        } catch (_: Exception) {}
    }

    /** Closes streams and terminates the recognition thread. */
    fun stopListening() {
        isWakeLoopRunning = false
        wakeThread?.join()
        audioRecord?.release()
        recognizer?.close()
        model?.close()
    }
}