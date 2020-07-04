package com.example.kotlinobservalo.Config

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.kotlinobservalo.Contexto
import com.example.kotlinobservalo.MainActivity
import com.example.kotlinobservalo.R


class LclObservaloConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val button: Preference? = findPreference("Item1")
            button?.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener {
                override fun onPreferenceClick(preference: Preference?): Boolean {
                    Configs.modoConfig = true
                    val intent = Intent(context, MainActivity::class.java)
                    context!!.startActivity(intent)
                    //2020-07-04 13:33:59.350 5630-5630/com.example.kotlinobservalo I/Choreographer: Skipped 109 frames!  The application may be doing too much work on its main thread.
                    return true
                }
            })

        }
    }
}


