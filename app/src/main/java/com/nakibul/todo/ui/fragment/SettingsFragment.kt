package com.nakibul.todo.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.nakibul.todo.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}