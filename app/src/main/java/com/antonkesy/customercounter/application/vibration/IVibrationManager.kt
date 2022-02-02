package com.antonkesy.customercounter.application.vibration

import com.antonkesy.customercounter.application.manager.IManager

interface IVibrationManager: IManager {
    fun vibrate()
}