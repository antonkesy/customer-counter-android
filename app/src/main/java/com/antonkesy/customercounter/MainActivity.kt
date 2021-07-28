package com.antonkesy.customercounter

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

    private var amountOfCustomers: Int = 100
    private var maxAmount: Int = 20000
    private lateinit var amountTV: TextView

    //switch when full
    private lateinit var customerIcon: ImageView
    private lateinit var stopTV: TextView
    private lateinit var limitTV: TextView

    //long press flag
    private var isLongPressAdd = false
    private var isLongPressSub = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //settings button
        findViewById<ImageButton>(R.id.settingsBtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //add button
        val addBtn = findViewById<ImageButton>(R.id.addBtn)
        addBtn.setOnClickListener {
            if (!isLongPressAdd) {
                changeCustomerAmount(1)
            }
            isLongPressAdd = false
        }
        addBtn.setOnLongClickListener {
            isLongPressAdd = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                while (isLongPressAdd) {
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
                changeCustomerAmount(-1)
            }
            isLongPressSub = false
        }
        subBtn.setOnLongClickListener {
            isLongPressSub = true
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                while (isLongPressSub) {
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


    }


    private fun changeCustomerAmount(value: Int) {
        val newAmount = amountOfCustomers + value
        //check if new amount is legal
        if (newAmount <= (maxAmount) && newAmount >= 0) {
            amountOfCustomers = newAmount
        }
        //get new color
        val newColor = when {
            amountOfCustomers >= maxAmount -> {
                R.color.red
            }
            amountOfCustomers >= maxAmount * .9 -> {
                R.color.yellow
            }
            else -> {
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
}
