package ufc.erv.garden.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.preference.PreferenceManager
import ufc.erv.garden.R
import ufc.erv.garden.databinding.SettingsBinding
import ufc.erv.garden.fragment.SettingsFragment

class SettingsActivity : DrawerBaseActivity() {
    private val vTAG = "SettingsActivity" /* Logger TAG */
    private lateinit var binding: SettingsBinding

    object EXTRAS {
        /* Booleano. Indica se a tela deve assumir que o usuário está logado */
        const val LOGGED_IN = "LOGGED_IN"
    }

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
        if (key == "dark_mode") {
            val value = pref.getBoolean(key, true)
            val mode = if (value) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

//    private fun getNightMode(config: Configuration): Boolean {
//        val uiFlag = config.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
//        return uiFlag == Configuration.UI_MODE_NIGHT_YES
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLoggedIn = intent.getBooleanExtra(EXTRAS.LOGGED_IN, true)

        binding = if (isLoggedIn){
            DataBindingUtil.inflate(layoutInflater, R.layout.settings, getRootForInflate(), true)
        } else {
            DataBindingUtil.setContentView(this, R.layout.settings)
        }
        binding.lifecycleOwner = this
        supportFragmentManager.commit {
            replace<SettingsFragment>(R.id.settings_fragment)
            setReorderingAllowed(true)
        }

        /* Listeners */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}