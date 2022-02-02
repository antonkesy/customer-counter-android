package com.antonkesy.customercounter.application.view

import android.widget.ImageButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ValueChangeButton(imageButton: ImageButton, valueChange: () -> Unit) {
    private var isLongPress = false

    init {
        with(imageButton) {
            setOnClickListener { singleClick(valueChange) }
            setOnLongClickListener { longClick(valueChange) }
        }
    }

    private fun longClick(valueChange: () -> Unit): Boolean {
        isLongPress = true
        GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
            var iteration = 0
            var delayTime = 150L
            //decrease delay time depending on how long button is clicked
            while (isLongPress) {
                if (iteration <= 15) {
                    ++iteration
                    delayTime = when (iteration) {
                        5 -> 100
                        15 -> 50
                        else -> delayTime
                    }
                }
                valueChange()
                delay(delayTime)
            }
        }
        return false
    }

    private fun singleClick(valueChange: () -> Unit) {
        if (!isLongPress) {
            valueChange()
        }
        isLongPress = false
    }

}