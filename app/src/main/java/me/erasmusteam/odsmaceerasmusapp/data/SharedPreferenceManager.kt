package me.erasmusteam.odsmaceerasmusapp.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler

class SharedPreferenceManager(context: Context) {

    private val preference = context.getSharedPreferences(
        context.packageName,
        Context.MODE_PRIVATE
    )

    private val editor = preference.edit()

    private val keyTheme = "theme"
    private val keyPreferredLang = "preferredLang"

    var theme
        get() = preference.getInt(keyTheme, 2)
        set(value){
            editor.putInt(keyTheme, value)
            editor.commit()
        }

    val themeFlag = arrayOf(
        AppCompatDelegate.MODE_NIGHT_NO,
        AppCompatDelegate.MODE_NIGHT_YES,
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    var preferredLang
        get() = preference.getString(keyPreferredLang, LocalizationHandler.DEFAULT_LANG_CODE)
        set(value){
            editor.putString(keyPreferredLang, value)
            editor.commit()
        }

}