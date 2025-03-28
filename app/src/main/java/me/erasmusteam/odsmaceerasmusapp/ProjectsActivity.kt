package me.erasmusteam.odsmaceerasmusapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import me.erasmusteam.odsmaceerasmusapp.objects.ActionBarDetails
import me.erasmusteam.odsmaceerasmusapp.fragments.WebViewFragment
import me.erasmusteam.odsmaceerasmusapp.interfaces.IExpandableListHolder
import me.erasmusteam.odsmaceerasmusapp.interfaces.IFragmentHolder

class ProjectsActivity : AppCompatActivity(), IFragmentHolder, IExpandableListHolder {

    lateinit var toggle : ActionBarDrawerToggle
    var header_username : String? = ""
    var jwt_token: String? = ""
    var is_admin: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.projects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bundle : Bundle? = intent.extras
        jwt_token = bundle?.getString("jwt_token")
        is_admin = bundle?.getBoolean("is_admin")
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

        setupExpandableListView(this, this@ProjectsActivity, jwt_token!!, is_admin!!, header_username!!)

        val webViewFragment: WebViewFragment = WebViewFragment.newInstance("https://sites.google.com/es-loule.edu.pt/mace")
        addFragmentToActivity(R.id.fragment_container_view, webViewFragment, supportFragmentManager)
    }

    override fun onRestart() {
        super.onRestart()

        setupExpandableListView(this, this@ProjectsActivity, jwt_token!!, is_admin!!, header_username!!)
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