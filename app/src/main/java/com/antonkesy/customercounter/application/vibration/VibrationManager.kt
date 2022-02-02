package com.antonkesy.customercounter.application.vibration

import android.view.HapticFeedbackConstants
import android.view.View

class VibrationManager(private val view: View?, private var isActive: Boolean) : IVibrationManager {
    override fun vibrate() {
        if (isActive) {
            view?.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
    }

    override fun setActive(isActive: Boolean) {
        this.isActive = isActive
    }
}