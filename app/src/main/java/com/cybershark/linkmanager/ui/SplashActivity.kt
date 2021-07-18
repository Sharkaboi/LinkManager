package com.cybershark.linkmanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            setDarkOrLightTheme()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private suspend fun setDarkOrLightTheme() = withContext(Dispatchers.Main) {
        val themeOptionAsync = async(Dispatchers.IO) {
            PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)
                .getBoolean("darkTheme", false)
        }
        val themeOption = themeOptionAsync.await()
        if (themeOption)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
