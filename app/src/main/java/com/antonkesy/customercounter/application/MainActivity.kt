package com.antonkesy.customercounter.application

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.antonkesy.customercounter.R
import com.antonkesy.customercounter.application.settings.ICustomerCounterSettings
import com.antonkesy.customercounter.application.settings.UserPreferencesManager
import com.antonkesy.customercounter.application.view.ValueChangeButton
import com.antonkesy.customercounter.counter.Counter
import com.antonkesy.customercounter.counter.ICounter


class MainActivity : AppCompatActivity() {

    private lateinit var settings: ICustomerCounterSettings
    private lateinit var counter: ICounter

    private lateinit var amountTV: TextView

    //switch when full
    private lateinit var customerIcon: ImageView
    private lateinit var stopTV: TextView
    private lateinit var limitTV: TextView

    //for volume button control
    private var view: View? = null
    private var audioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById<View>(android.R.id.content).rootView
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        settings = UserPreferencesManager(this)
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
        customerIcon = findViewById(R.id.customerIcon)
        stopTV = findViewById(R.id.stopTV)
        limitTV = findViewById(R.id.limitReachedTV)
    }

    private fun setupSubButton() {
        ValueChangeButton(findViewById(R.id.addBtn)) {
            playClickSound()
            vibrateClick()
            counter.decrement()
        }
    }

    private fun setupAddButton() {
        ValueChangeButton(findViewById(R.id.addBtn)) {
            playClickSound()
            vibrateClick()
            counter.increment()
        }
    }

    private fun setupSettingsButton() {
        findViewById<ImageButton>(R.id.settingsBtn).setOnClickListener {
            playClickSound()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        updateUIColor()
        updateCounterUI()
        counter.setMax(settings.getMaxCustomer())
        super.onResume()
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

    private fun playClickSound() {
        if (settings.isSoundActive()) {
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (settings.isVolumeControlButton()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                vibrateClick()
                playClickSound()
                counter.decrement()
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                vibrateClick()
                playClickSound()
                counter.increment()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateCounterUI() {
        //get new color + trick programming also change customer icon!
        val newColor = when {
            counter.getCurrentAmount() >= counter.getMax() -> {
                R.color.red
            }
            counter.getCurrentAmount() >= counter.getMax() * .9 -> {
                //side effect!
                customerIcon.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24_yellow)
                R.color.yellow
            }
            else -> {
                //side effect!
                customerIcon.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24_green)
                R.color.green
            }
        }
        //set text color
        amountTV.setTextColor(
            ContextCompat.getColor(
                this, newColor
            )
        )
        //set statusBar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, newColor)
        }
        //set ui warnings
        val isFull = counter.getCurrentAmount() >= counter.getMax()
        customerIcon.visibility = if (isFull) View.GONE else View.VISIBLE
        stopTV.visibility = if (isFull) View.VISIBLE else View.GONE
        limitTV.visibility = if (isFull) View.VISIBLE else View.GONE
        //set value in textView
        amountTV.text = (counter.getCurrentAmount().toString() + "\\" + counter.getMax().toString())

    }
}
