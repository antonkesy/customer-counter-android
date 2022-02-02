package com.antonkesy.customercounter.application.view.responsive.wrapper

import android.widget.TextView
import androidx.annotation.ColorInt
import com.antonkesy.customercounter.application.view.responsive.IColorChanger

class TextViewWrapper(private val textView: TextView) : IColorChanger {
    override fun changeColor(@ColorInt color: Int) {
        textView.setTextColor(color)
    }
}