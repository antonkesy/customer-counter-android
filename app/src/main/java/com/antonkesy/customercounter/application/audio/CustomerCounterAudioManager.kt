package com.antonkesy.customercounter.application.audio

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity

class CustomerCounterAudioManager(context: AppCompatActivity, private var soundActive: Boolean) :
    ICustomerCounterAudioManager {
    private var audioManager: AudioManager? =
        context.getSystemService(AUDIO_SERVICE) as AudioManager

    override fun playClickSound() {
        if (soundActive)
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
    }

    override fun setActive(isActive: Boolean) {
        soundActive = isActive
    }
}