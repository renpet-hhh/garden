package ufc.erv.garden.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ufc.erv.garden.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}