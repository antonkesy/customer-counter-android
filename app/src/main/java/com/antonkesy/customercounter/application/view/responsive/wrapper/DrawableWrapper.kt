package com.antonkesy.customercounter.application.view.responsive.wrapper

import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.antonkesy.customercounter.application.view.responsive.IColorChanger

class DrawableWrapper(private val view: View) : IColorChanger {
    override fun changeColor(@ColorInt color: Int) {
        val unwrappedDrawable = view.background
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, color)
        view.background = wrappedDrawable
    }
}