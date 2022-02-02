package com.antonkesy.customercounter.application

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.antonkesy.customercounter.R
import com.antonkesy.customercounter.application.audio.CustomerCounterAudioManager
import com.antonkesy.customercounter.application.audio.ICustomerCounterAudioManager
import com.antonkesy.customercounter.application.settings.ICustomerCounterSettings
import com.antonkesy.customercounter.application.settings.UserPreferencesManager
import com.antonkesy.customercounter.application.view.ValueChangeButton
import com.antonkesy.customercounter.application.view.responsive.IVisibilityToggle
import com.antonkesy.customercounter.application.view.responsive.VisibilityToggle
import com.antonkesy.customercounter.counter.Counter
import com.antonkesy.customercounter.counter.ICounter


class MainActivity : AppCompatActivity() {

    private lateinit var settings: ICustomerCounterSettings
    private lateinit var counter: ICounter
    private lateinit var audioManager: ICustomerCounterAudioManager

    private lateinit var amountTV: TextView

    //switch when full
    private lateinit var toggleItems: List<IVisibilityToggle>

    //for volume button control
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById<View>(android.R.id.content).rootView
        settings = UserPreferencesManager(this)
        audioManager = CustomerCounterAudioManager(this, settings.isSoundActive())
        counter =
            Counter(settings.getCustomerAmount(), settings.getMaxCustomer(), this::updateCounterUI)

        updateUIColor()

        setupSettingsButton()
        setupAddButton()
        setupSubButton()

        setViewElements()
    }

    private fun setViewElements() {
        amountTV = findViewById(R.id.amountTV)
        toggleItems = listOf(
            VisibilityToggle(findViewById(R.id.customerIcon), GONE, VISIBLE),
            VisibilityToggle(findViewById(R.id.stopTV), VISIBLE, GONE),
            VisibilityToggle(findViewById(R.id.limitReachedTV), VISIBLE, GONE),
        )
    }

    private fun setupSubButton() {
        ValueChangeButton(findViewById(R.id.subBtn)) {
            audioManager.playClickSound()
            vibrateClick()
            counter.decrement()
        }
    }

    private fun setupAddButton() {
        ValueChangeButton(findViewById(R.id.addBtn)) {
            audioManager.playClickSound()
            vibrateClick()
            counter.increment()
        }
    }

    private fun setupSettingsButton() {
        findViewById<ImageButton>(R.id.settingsBtn).setOnClickListener {
            audioManager.playClickSound()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        updateUIColor()
        updateCounterUI()
        audioManager.setActive(settings.isSoundActive())
        counter.setMax(settings.getMaxCustomer())
        super.onResume()
    }

    override fun onDestroy() {
        settings.putCustomerAmount(counter.getCurrentAmount())
        super.onDestroy()
    }

    override fun onPause() {
        settings.putCustomerAmount(counter.getCurrentAmount())
        super.onPause()
    }

    private fun updateUIColor() {
        //change settings button color for low api with drawable change
        updateImageButtonDrawable(
            findViewById(R.id.settingsBtn), if (settings.isDarkMode())
                R.drawable.ic_baseline_settings_24_white
            else R.drawable.ic_baseline_settings_24_black
        )

        updateImageButtonDrawable(
            findViewById(R.id.subBtn), if (settings.isDarkMode())
                R.drawable.ic_baseline_remove_24_white
            else R.drawable.ic_baseline_remove_24_black
        )
        //change sub button color for low api with drawable change
        updateImageButtonDrawable(
            findViewById(R.id.addBtn), if (settings.isDarkMode())
                R.drawable.ic_baseline_add_24_white
            else R.drawable.ic_baseline_add_24_black
        )

        //update background color
        findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(
            ContextCompat.getColor(
                this, if (settings.isDarkMode())
                    R.color.black
                else R.color.white
            )
        )
    }

    private fun updateImageButtonDrawable(imgBtn: ImageButton, @DrawableRes drawable: Int) {
        imgBtn.setImageDrawable(ContextCompat.getDrawable(this, drawable))
    }

    private fun vibrateClick() {
        if (settings.isVibrationActive()) {
            view?.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (settings.isVolumeControlButton()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                vibrateClick()
                audioManager.playClickSound()
                counter.decrement()
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                vibrateClick()
                audioManager.playClickSound()
                counter.increment()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateCounterUI() {
        val newColor = getCustomersStateColor()
        setPersonIconColor(newColor)
        updateCustomerAmountTextView(newColor)
        updateStatusBar(newColor)
        updateToggleViews()
    }

    private fun updateToggleViews() {
        val isFull = counter.getCurrentAmount() >= counter.getMax()
        toggleItems.forEach { t -> t.onStateChange(isFull) }
    }

    private fun updateStatusBar(newColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = newColor
        }
    }

    private fun updateCustomerAmountTextView(newColor: Int) {
        amountTV.setTextColor(newColor)
        amountTV.text = (counter.getCurrentAmount().toString() + "\\" + counter.getMax().toString())
    }

    private fun setPersonIconColor(newColor: Int) {
        val unwrappedDrawable =
            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_person_24_green)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, newColor)
    }

    private fun getCustomersStateColor() = ContextCompat.getColor(
        this, when {
            counter.getCurrentAmount() >= counter.getMax() -> R.color.red
            counter.getCurrentAmount() >= counter.getMax() * .9 -> R.color.yellow
            else -> R.color.green
        }
    )
}
