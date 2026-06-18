package com.ememisya.fallassistant

import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class FallAssistantSessionService : VoiceInteractionSessionService() {

    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return SafeNoOpSession(this)
    }

    private class SafeNoOpSession(private val resContext: Context) : VoiceInteractionSession(resContext) {

        override fun onShow(args: Bundle?, showFlags: Int) {
            super.onShow(args, showFlags)
            finish()
        }
    }
}