package me.erasmusteam.odsmaceerasmusapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import me.erasmusteam.odsmaceerasmusapp.data.SharedPreferenceManager

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferenceManager = SharedPreferenceManager(this)
        AppCompatDelegate.setDefaultNightMode(sharedPreferenceManager.themeFlag[sharedPreferenceManager.theme])
    }
}