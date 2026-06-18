package com.ememisya.fallassistant


import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionService
import android.speech.SpeechRecognizer

class FallAssistantRecognitionService : RecognitionService() {

    override fun onStartListening(intent: Intent?, callback: Callback?) {
        callback?.readyForSpeech(Bundle.EMPTY)

        val emptyResults = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(""))
        }
        callback?.results(emptyResults)
    }

    override fun onStopListening(callback: Callback?) {}

    override fun onCancel(callback: Callback?) {}
}
