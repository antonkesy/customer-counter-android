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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.antonkesy.customercounter.R
import com.antonkesy.customercounter.application.settings.ICustomerCounterSettings
import com.antonkesy.customercounter.application.settings.UserPreferencesManager
import com.antonkesy.customercounter.counter.Counter
import com.antonkesy.customercounter.counter.ICounter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var settings: ICustomerCounterSettings
    private lateinit var counter: ICounter

    private lateinit var amountTV: TextView

    //switch when full
    private lateinit var customerIcon: ImageView
    private lateinit var stopTV: TextView
    private lateinit var limitTV: TextView

    //long press flag
    private var isLongPressAdd = false
    private var isLongPressSub = false

    //settings flags
    private var isSoundOn = true
    private var isVibrateOn = true
    private var isVolumeButtonControlOn = false
    private var isDarkMode = false

    //for volume button control
    private var view: View? = null
    private var audioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById<View>(android.R.id.content).rootView
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        settings = UserPreferencesManager(this)
        counter = Counter(settings.getCustomerAmount(), settings.getMaxCustomer(),  this::updateCounterUI)
        updateFromPreferences()
        updateUIColor()

        //settings button
        findViewById<ImageButton>(R.id.settingsBtn).setOnClickListener {
            checkSoundPlayClick()
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //add button
        val addBtn = findViewById<ImageButton>(R.id.addBtn)
        addBtn.setOnClickListener {
            if (!isLongPressAdd) {
                checkSoundPlayClick()
                checkVibrateClick()
                counter.increment()
            }
            isLongPressAdd = false
        }
        addBtn.setOnLongClickListener {
            isLongPressAdd = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                var iteration = 0
                var delayTime = 150L
                //decrease delay time depending on how long button is clicked
                while (isLongPressAdd) {
                    if (iteration <= 15) {
                        ++iteration
                        delayTime = when (iteration) {
                            5 -> 100
                            15 -> 50
                            else -> delayTime
                        }
                    }
                    checkSoundPlayClick()
                    checkVibrateClick()
                    counter.increment()
                    delay(delayTime)
                }
            }
            false
        }

        //sub button
        val subBtn = findViewById<ImageButton>(R.id.subBtn)
        subBtn.setOnClickListener {
            if (!isLongPressSub) {
                checkSoundPlayClick()
                checkVibrateClick()
                counter.decrement()
            }
            isLongPressSub = false
        }
        subBtn.setOnLongClickListener {
            isLongPressSub = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                var iteration = 0
                var delayTime = 150L
                //decrease delay time depending on how long button is clicked
                while (isLongPressSub) {
                    if (iteration <= 15) {
                        ++iteration
                        delayTime = when (iteration) {
                            5 -> 100
                            15 -> 50
                            else -> delayTime
                        }
                    }
                    checkSoundPlayClick()
                    checkVibrateClick()
                    counter.decrement()
                    delay(delayTime)
                }
            }
            false
        }

        amountTV = findViewById(R.id.amountTV)
        customerIcon = findViewById(R.id.customerIcon)
        stopTV = findViewById(R.id.stopTV)
        limitTV = findViewById(R.id.limitReachedTV)

    }

    override fun onResume() {
        updateFromPreferences()
        updateUIColor()
        updateCounterUI()
        counter.setMax(settings.getMaxCustomer())
        super.onResume()
    }

    override fun onPause() {
        settings.setCustomerAmount(counter.getCurrentAmount())
        super.onPause()
    }

    private fun updateFromPreferences() {
        isSoundOn = settings.isSoundActive()
        isVibrateOn = settings.isVibrationActive()
        isVolumeButtonControlOn = settings.isVolumeControlButton()
        isDarkMode = settings.isDarkMode()
    }

    private fun updateUIColor() {
        //change settings button color for low api with drawable change
        findViewById<ImageButton>(R.id.settingsBtn).setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isDarkMode)
                    R.drawable.ic_baseline_settings_24_white
                else R.drawable.ic_baseline_settings_24_black
            )
        )
        //change sub button color for low api with drawable change
        findViewById<ImageButton>(R.id.subBtn).setImageDrawable(
            ContextCompat.getDrawable(
                this, if (isDarkMode)
                    R.drawable.ic_baseline_remove_24_white
                else R.drawable.ic_baseline_remove_24_black
            )
        )
        //change sub button color for low api with drawable change
        findViewById<ImageButton>(R.id.addBtn).setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isDarkMode)
                    R.drawable.ic_baseline_add_24_white
                else R.drawable.ic_baseline_add_24_black
            )
        )
        //update background color
        findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(
            ContextCompat.getColor(
                this, if (isDarkMode)
                    R.color.black
                else R.color.white
            )
        )
    }

    private fun checkVibrateClick() {
        if (isVibrateOn) {
            view?.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
    }

    private fun checkSoundPlayClick() {
        if (isSoundOn) {
            //todo fix sound not playing
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isVolumeButtonControlOn) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                checkVibrateClick()
                checkSoundPlayClick()
                counter.decrement()
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                checkVibrateClick()
                checkSoundPlayClick()
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
