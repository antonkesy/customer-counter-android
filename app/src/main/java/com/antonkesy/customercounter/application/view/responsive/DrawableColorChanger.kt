package com.antonkesy.customercounter.application.view.responsive

import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

class DrawableColorChanger(private val view: View) : IDrawableColorChanger {
    override fun setDrawableColor(@ColorInt color: Int) {
        val unwrappedDrawable = view.background
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, color)
        view.background = wrappedDrawable
    }
}