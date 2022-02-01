package com.antonkesy.customercounter.application.settings

interface IApplicationSettings {
    fun isVibrationActive(): Boolean
    fun putVibrationActive(isActive: Boolean)
    fun isSoundActive(): Boolean
    fun putSoundActive(isActive: Boolean)
    fun isVolumeControlButton(): Boolean
    fun putVolumeControlButton(isButton: Boolean)
    fun isDarkMode(): Boolean
    fun putDarkMode(isDarkMode: Boolean)

    fun getDarkModeKey(): String
}