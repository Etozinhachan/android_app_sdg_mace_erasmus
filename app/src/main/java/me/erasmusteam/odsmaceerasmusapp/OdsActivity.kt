package me.erasmusteam.odsmaceerasmusapp

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import me.erasmusteam.odsmaceerasmusapp.adapters.SDGAdapter
import me.erasmusteam.odsmaceerasmusapp.data.SDGData
import me.erasmusteam.odsmaceerasmusapp.interfaces.IExpandableListHolder
import me.erasmusteam.odsmaceerasmusapp.objects.ActionBarDetails
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class OdsActivity : AppCompatActivity(), IExpandableListHolder {

    lateinit var toggle : ActionBarDrawerToggle
    var header_username : String? = null
    var jwt_token : String? = ""
    var is_admin : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.ods)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bundle : Bundle? = intent.extras
        jwt_token = bundle?.getString("jwt_token")
        is_admin = bundle?.getBoolean("is_admin")
        header_username = bundle?.getString("username")

        /*

        val url = "https://sdgs.un.org/sites/default/files/2023-08/SDG_report_2023_infographics_Goal%201.jpg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

         */

        val SDGsRecyclerView: RecyclerView = findViewById(R.id.goalsRecyclerView)

        val SDGs = listOf(
            SDGData(R.drawable.e_web_goal_01, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_02, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_03, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_04, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_05, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_06, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_07, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_08, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_09, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_10, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_11, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_12, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_13, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_14, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_15, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_16, R.raw.ways_to_self_soothe),
            SDGData(R.drawable.e_web_goal_17, R.raw.ways_to_self_soothe)
        )

        SDGsRecyclerView.layoutManager = LinearLayoutManager(this)
        SDGsRecyclerView.adapter = SDGAdapter(SDGs, this@OdsActivity)

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

        setupExpandableListView(this, this@OdsActivity, jwt_token!!, is_admin!!, header_username!!)

    }

    override fun onRestart() {
        super.onRestart()

        setupExpandableListView(this, this@OdsActivity, jwt_token!!, is_admin!!, header_username!!)
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