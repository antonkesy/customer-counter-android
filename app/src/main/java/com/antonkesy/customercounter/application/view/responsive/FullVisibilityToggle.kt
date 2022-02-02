package com.antonkesy.customercounter.application.view.responsive

import android.view.View

open class FullVisibilityToggle(
    private val view: View,
    private val onFull: Int,
    private val onNotFull: Int
) : IVisibilityToggle {

    override fun onStateChange(isActive: Boolean) {
        view.visibility = if (isActive) onFull else onNotFull
    }
}