package com.antonkesy.customercounter.application.audio

import com.antonkesy.customercounter.application.manager.IManager

interface ICustomerCounterAudioManager : IManager {
    fun playClickSound()
}