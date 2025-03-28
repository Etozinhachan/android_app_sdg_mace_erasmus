package me.erasmusteam.odsmaceerasmusapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import me.erasmusteam.odsmaceerasmusapp.objects.ActionBarDetails
import me.erasmusteam.odsmaceerasmusapp.data.SharedPreferenceManager
import me.erasmusteam.odsmaceerasmusapp.interfaces.IDarkModeChangerHolder
import me.erasmusteam.odsmaceerasmusapp.interfaces.IExpandableListHolder
import me.erasmusteam.odsmaceerasmusapp.interfaces.ITranslationHolder
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler

class SettingsActivity : AppCompatActivity(), OnItemSelectedListener, IExpandableListHolder, IDarkModeChangerHolder, ITranslationHolder {

    lateinit var toggle : ActionBarDrawerToggle
    var jwt_token : String? = null
    var is_admin: Boolean? = null
    var header_username : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val darkModeType = checkDarkModeType(this@SettingsActivity)
        val useDarkTheme = darkModeType != AppCompatDelegate.MODE_NIGHT_NO

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        forceUpdateLangUsed(this, this@SettingsActivity, SharedPreferenceManager(this).preferredLang.toString(), listOf(
            findViewById(R.id.SettingsTxt),
            findViewById(R.id.switchMode)
        ))

        val bundle : Bundle? = intent.extras
        jwt_token = bundle?.getString("jwt_token")
        is_admin = bundle?.getBoolean("is_admin")
        header_username = bundle?.getString("username")

        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        val navView : NavigationView = findViewById(R.id.nav_view)

        val modeSwitcher : SwitchCompat = findViewById(R.id.switchMode)

        modeSwitcher.isChecked = useDarkTheme

        val langSpinner: Spinner = findViewById(R.id.langSpinner)
        langSpinner.onItemSelectedListener = this

        val ad :ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, LocalizationHandler.return_lang_codes_available())

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        langSpinner.adapter = ad

        for (i in 0 until LocalizationHandler.return_lang_codes_available().size){
            val cur_loop_lang = LocalizationHandler.return_lang_codes_available()[i]
            if (cur_loop_lang == SharedPreferenceManager(this).preferredLang.toString()){
                langSpinner.setSelection(i)
                break
            }
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val actionBar : ActionBar? = supportActionBar

        actionBar?.title = ActionBarDetails.title
        actionBar?.setIcon(ActionBarDetails.icon)

        actionBar?.setDisplayUseLogoEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupExpandableListView(this, this@SettingsActivity, jwt_token!!, is_admin!!, header_username!!)

        modeSwitcher.setOnCheckedChangeListener { buttonView, isChecked ->
            updatePreferredTheme(isChecked)
        }

       /* modeSwitcher.setOnClickListener{
            modeSwitcher.isChecked = !modeSwitcher.isChecked
            updatePreferredTheme(modeSwitcher.isChecked)
        }*/


    }

    override fun onRestart() {
        super.onRestart()

        setupExpandableListView(this, this@SettingsActivity, jwt_token!!, is_admin!!, header_username!!)
    }

    private fun updatePreferredTheme(useDarkMode: Boolean){

        val sharedPreferenceManager = SharedPreferenceManager(this)

        if (useDarkMode){
            sharedPreferenceManager.theme = 1
        }else{
            sharedPreferenceManager.theme = 0
        }

        AppCompatDelegate.setDefaultNightMode(sharedPreferenceManager.themeFlag[sharedPreferenceManager.theme])

        //val intent = Intent(this@SettingsActivity, SettingsActivity::class.java)
        //finish()
        //startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val header_username_tv : TextView = findViewById(R.id.header_username)
        header_username_tv.text = header_username
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){

            return true

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val lang_code_selected = LocalizationHandler.return_lang_codes_available()[position]

        val updatedLangUsed = updateLangUsed(this, this@SettingsActivity, lang_code_selected, listOf(
            findViewById(R.id.SettingsTxt),
            findViewById(R.id.switchMode)
        ))

        if (updatedLangUsed){
            println("Batata de arroz")
            onRestart()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}