package com.antonkesy.customercounter.application.audio

interface ICustomerCounterAudioManager {
    fun playClickSound()
    fun setActive(isActive: Boolean)
}