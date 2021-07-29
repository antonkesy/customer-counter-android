package com.antonkesy.customercounter

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var amountOfCustomers: Int = 0
    private var maxAmount: Int = 10
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

    //for volume button control
    private var view: View? = null
    private var audioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById<View>(android.R.id.content).rootView
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
                changeCustomerAmount(1)
            }
            isLongPressAdd = false
        }
        addBtn.setOnLongClickListener {
            isLongPressAdd = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                while (isLongPressAdd) {
                    checkSoundPlayClick()
                    checkVibrateClick()
                    changeCustomerAmount(1)
                    delay(150)
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
                changeCustomerAmount(-1)
            }
            isLongPressSub = false
        }
        subBtn.setOnLongClickListener {
            isLongPressSub = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                while (isLongPressSub) {
                    checkSoundPlayClick()
                    checkVibrateClick()
                    changeCustomerAmount(-1)
                    delay(150)
                }
            }
            false
        }

        amountTV = findViewById(R.id.amountTV)
        customerIcon = findViewById(R.id.customerIcon)
        stopTV = findViewById(R.id.stopTV)
        limitTV = findViewById(R.id.limitReachedTV)

        updateFromPreferences()
        changeCustomerAmount(0)
    }


    private fun changeCustomerAmount(value: Int) {
        val newAmount = amountOfCustomers + value
        //check if new amount is legal
        if (newAmount >= 0) {
            amountOfCustomers = newAmount
        }
        //get new color + trick programming also change customer icon!
        val newColor = when {
            amountOfCustomers >= maxAmount -> {
                R.color.red
            }
            amountOfCustomers >= maxAmount * .9 -> {
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
        val isFull = amountOfCustomers >= maxAmount
        customerIcon.visibility = if (isFull) View.GONE else View.VISIBLE
        stopTV.visibility = if (isFull) View.VISIBLE else View.GONE
        limitTV.visibility = if (isFull) View.VISIBLE else View.GONE
        //set value in textView
        amountTV.text = (amountOfCustomers.toString() + "\\" + maxAmount.toString())
    }

    override fun onPause() {
        UserPreferencesManager.setCustomerAmount(this, amountOfCustomers)
        super.onPause()
    }

    override fun onResume() {
        updateFromPreferences()
        updateUIColor()
        changeCustomerAmount(0)
        super.onResume()
    }

    private fun updateFromPreferences() {
        maxAmount = UserPreferencesManager.getMaxCustomer(this)
        amountOfCustomers = UserPreferencesManager.getCustomerAmount(this)
        isSoundOn = UserPreferencesManager.isSoundOn(this)
        isVibrateOn = UserPreferencesManager.isVibrateOn(this)
        isVolumeButtonControlOn = UserPreferencesManager.isVolumeControlOn(this)
    }

    private fun updateUIColor() {
        //change settings button color for low api with drawable change
        findViewById<ImageButton>(R.id.settingsBtn).setImageDrawable(
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_settings_24_white)
                }
                else -> ContextCompat.getDrawable(this, R.drawable.ic_baseline_settings_24_black)
            }
        )
        //change sub button color for low api with drawable change
        findViewById<ImageButton>(R.id.subBtn).setImageDrawable(
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_24_white)
                }
                else -> ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_24_black)
            }
        )

        //change sub button color for low api with drawable change
        findViewById<ImageButton>(R.id.addBtn).setImageDrawable(
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_add_24_white)
                }
                else -> ContextCompat.getDrawable(this, R.drawable.ic_baseline_add_24_black)
            }
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
                changeCustomerAmount(-1)
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                checkVibrateClick()
                checkSoundPlayClick()
                changeCustomerAmount(1)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
