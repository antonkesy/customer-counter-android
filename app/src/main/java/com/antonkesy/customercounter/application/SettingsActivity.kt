package com.antonkesy.customercounter.application

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.antonkesy.customercounter.R
import com.antonkesy.customercounter.application.billing.IDonateManager
import com.antonkesy.customercounter.application.settings.UserPreferencesManager


class SettingsActivity : AppCompatActivity() {

    private var donateManager: IDonateManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        findViewById<Button>(R.id.donateBtn).setOnClickListener { donateManager?.startDonatePurchasePrompt() }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //set number edit pref to only number dial
        val userPreferencesManager = UserPreferencesManager(context)
        setNumberInputPref(preferenceManager.findPreference(userPreferencesManager.getCustomerKey())!!)
        setNumberInputPref(preferenceManager.findPreference(userPreferencesManager.getMaxCustomerKey())!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myView = super.onCreateView(inflater, container, savedInstanceState)

        val userPreferencesManager = UserPreferencesManager(context)
        updateBackgroundColor(myView, userPreferencesManager.isDarkMode())

        val darkModeSwitch: SwitchPreferenceCompat? =
            findPreference(userPreferencesManager.getDarkModeKey())
        darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            updateBackgroundColor(myView, newValue as Boolean)
            true
        }

        return myView
    }

    private fun updateBackgroundColor(view: View, darkMode: Boolean) {
        view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), if (darkMode)
                    R.color.grey
                else
                    R.color.white
            )
        )
    }

    /**
     * changes EditTextPreference input to number only
     */
    private fun setNumberInputPref(editTextPreference: EditTextPreference) {
        editTextPreference.setOnBindEditTextListener { editText ->
            editText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
    }
}
