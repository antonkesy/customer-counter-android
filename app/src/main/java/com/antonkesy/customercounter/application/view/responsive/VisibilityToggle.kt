package com.antonkesy.customercounter.application.view.responsive

import android.view.View

class VisibilityToggle(
    private val view: View,
    private val onFull: Int,
    private val onNotFull: Int
) : IVisibilityToggle {

    override fun onStateChange(isActive: Boolean) {
        view.visibility = if (isActive) onFull else onNotFull
    }
}