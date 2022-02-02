package com.antonkesy.customercounter.application.view.responsive.wrapper

import android.os.Build
import android.view.Window
import androidx.annotation.ColorInt
import com.antonkesy.customercounter.application.view.responsive.IColorChanger

class StatusBarWrapper(private val window: Window) : IColorChanger {
    override fun changeColor(@ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }
}