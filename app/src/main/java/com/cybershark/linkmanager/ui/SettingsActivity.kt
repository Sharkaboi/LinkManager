package com.cybershark.linkmanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cybershark.linkmanager.BuildConfig
import com.cybershark.linkmanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setCustomAnimations()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun setCustomAnimations() = overridePendingTransition(
        R.anim.slide_in_left,
        R.anim.slide_out_right
    )

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setVersionCode()
            setDeleteOnClick()
            setSyncOptionPrefs()
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
            MaterialAlertDialogBuilder(this.context)
                .setTitle(R.string.delete_links)
                .setMessage(R.string.delete_alert)
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.delete) { _, _ ->
                    //sending RESULT_OK to links fragment to delete all links
                    activity?.setResult(Activity.RESULT_OK, Intent())
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        private fun setVersionCode() {
            findPreference<Preference>("versionCode")?.summary =
                BuildConfig.VERSION_CODE.toFloat().toString()
        }
    }
}