package com.cybershark.linkmanager.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cybershark.linkmanager.BuildConfig
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val linksViewModel by activityViewModels<LinksViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVersionCode()
        setDeleteOnClick()
        setSyncOptionPrefs()
        setDarkThemePrefs()
    }

    private fun setDarkThemePrefs() {
        findPreference<Preference>("darkTheme")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            true
        }
    }

    private fun setSyncOptionPrefs() {
        val syncPrefs = findPreference<Preference>("syncOption")
        syncPrefs?.isEnabled = android.os.Build.VERSION.SDK_INT >= 28
        syncPrefs?.setOnPreferenceClickListener {
            startActivity(Intent(Settings.ACTION_PRIVACY_SETTINGS))
            true
        }
    }

    private fun setDeleteOnClick() {
        findPreference<Preference>("deleteAllLinks")?.setOnPreferenceClickListener {
            openDeleteAlertDialog()
            true
        }
    }

    private fun openDeleteAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_links)
            .setMessage(R.string.delete_alert)
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                linksViewModel.deleteAllLinks()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setVersionCode() {
        findPreference<Preference>("versionCode")?.summary =
            "v" + BuildConfig.VERSION_NAME
    }
}