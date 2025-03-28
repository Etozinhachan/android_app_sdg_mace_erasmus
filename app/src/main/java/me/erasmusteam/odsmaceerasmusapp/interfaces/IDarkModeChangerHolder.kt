package me.erasmusteam.odsmaceerasmusapp.interfaces

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import me.erasmusteam.odsmaceerasmusapp.data.SharedPreferenceManager

interface IDarkModeChangerHolder {

    fun checkAndSetDarkMode(darkModeHolderActivity: AppCompatActivity): Boolean{
        val sharedPref = darkModeHolderActivity.getPreferences(Context.MODE_PRIVATE)
        val useDarkTheme : Boolean = sharedPref.getBoolean("useDarkTheme", false)

        println("this was runned")

        if (useDarkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            return true
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        return false
    }

    fun checkDarkModeType(darkModeHolderActivity: AppCompatActivity): Int{
        val sharedPreferenceManager = SharedPreferenceManager(darkModeHolderActivity)

        return sharedPreferenceManager.themeFlag[sharedPreferenceManager.theme]
    }

    fun setDarkModeType(darkModeHolderActivity: AppCompatActivity, darkModeType: Int){
        val sharedPreferenceManager = SharedPreferenceManager(darkModeHolderActivity)
        sharedPreferenceManager.theme = darkModeType
    }

}