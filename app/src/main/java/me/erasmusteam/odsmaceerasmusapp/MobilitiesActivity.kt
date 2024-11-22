package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MobilitiesActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    var header_username : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.mobilities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle : Bundle? = intent.extras
        val jwt_token : String? = bundle?.getString("jwt_token")
        val is_admin : String? = bundle?.getString("is_admin")
        header_username = bundle?.getString("username")

        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val actionBar : ActionBar? = supportActionBar

        actionBar?.title = ActionBarDetails.title
        actionBar?.setIcon(ActionBarDetails.icon)

        actionBar?.setDisplayUseLogoEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_project -> {

                    val intent : Intent = Intent(this, ProjectsActivity::class.java)

                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin)
                    intent.putExtra("username", header_username)

                    startActivity(intent)

                }
                R.id.nav_ods -> {

                    val intent : Intent = Intent(this, OdsActivity::class.java)

                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin)
                    intent.putExtra("username", header_username)

                    startActivity(intent)

                }
                R.id.nav_mobilities -> {}
                R.id.nav_activities -> {

                    val intent : Intent = Intent(this, ActivitiesActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin)
                    intent.putExtra("username", header_username)

                    startActivity(intent)

                }
                R.id.nav_settings -> {
                    val intent : Intent = Intent(this, SettingsActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin)
                    intent.putExtra("username", header_username)

                    startActivity(intent)
                }
                R.id.nav_login -> {

                    val intent : Intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)

                }


            }

            true

        }

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

}