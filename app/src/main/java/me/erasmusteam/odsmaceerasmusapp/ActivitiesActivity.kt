package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import me.erasmusteam.odsmaceerasmusapp.objects.ActionBarDetails
import me.erasmusteam.odsmaceerasmusapp.data.ActivityData
import me.erasmusteam.odsmaceerasmusapp.data.ActivityDetailsStruct
import me.erasmusteam.odsmaceerasmusapp.adapters.ActivityRecyclerViewAdapter
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler
import me.erasmusteam.odsmaceerasmusapp.requests.MultipartResponseRequest
import me.erasmusteam.odsmaceerasmusapp.interfaces.IExpandableListHolder
import me.erasmusteam.odsmaceerasmusapp.interfaces.RecyclerViewEvent
import org.json.JSONException
import org.json.JSONObject


class ActivitiesActivity : AppCompatActivity(), IExpandableListHolder, RecyclerViewEvent {

    var button_id_map : MutableMap<String, Int> = mutableMapOf()
    var activity_map : MutableMap<Int, JSONObject> = mutableMapOf()
    var jwt_token : String? = ""
    var header_username : String? = ""
    lateinit var toggle : ActionBarDrawerToggle
    var activityDataList = mutableListOf<ActivityData>()
    var filteredActivities = mutableListOf<ActivityData>()
    lateinit var activityAdapter: ActivityRecyclerViewAdapter
    var is_admin : Boolean = false

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle : Bundle? = intent.extras
        jwt_token = bundle?.getString("jwt_token")
        is_admin = bundle?.getBoolean("is_admin")!!
        header_username = bundle?.getString("username")

        //scroll_view_activities = findViewById(R.id.linearScrollActivities)

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

        val addFAB : FloatingActionButton = findViewById(R.id.add_activity_button)

        recyclerView = findViewById(R.id.recyclerViewActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val chipGroup: ChipGroup = findViewById(R.id.chipGroupFilters)
        chipGroup.setOnCheckedStateChangeListener{ group, checkedIds ->
            var chip: Chip? = null
            if (checkedIds.size > 0){
                chip = group.findViewById<Chip>(checkedIds[0])
            }
            if (chip != null){
                applyFilter(chip.text.toString())
            }else{
                filteredActivities.clear()
                filteredActivities.addAll(activityDataList)
                activityAdapter.notifyDataSetChanged()
            }
        }

        addFAB.setOnClickListener(){
            val intent : Intent = Intent(this, AddActivitiesActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            intent.putExtra("jwt_token", jwt_token)

            startActivity(intent)
        }

        if (jwt_token == "guest"){
            addFAB.visibility = View.INVISIBLE
        }

        setupExpandableListView(this, this@ActivitiesActivity, jwt_token!!, is_admin, header_username!!)


        getActivitiesAndUpdateUI()

    }

    override fun onRestart() {
        super.onRestart()

        setupExpandableListView(this, this@ActivitiesActivity, jwt_token!!, is_admin, header_username!!)
        getActivitiesAndUpdateUI()
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

    private fun getActivitiesAndUpdateUI(){
        val queue : RequestQueue = Volley.newRequestQueue(this)
        val url : String ="${ApiDetails.url}api/Activity"

        button_id_map = mutableMapOf()
        activity_map = mutableMapOf()

        val multipartResponseRequest : MultipartResponseRequest = MultipartResponseRequest( url, emptyMap(),
            { response ->
                try {
                    var n_btn : Int = 0
                    //println(response.length())

                    activityDataList = response.filter { activityData ->

                        if (!is_admin){
                            if (activityData.activity_state != 2) {
                                return@filter false
                            }
                        }

                        true

                    }.toMutableList()

                    activityAdapter = ActivityRecyclerViewAdapter(filteredActivities, this)
                    recyclerView.adapter = activityAdapter

                    filteredActivities.clear()
                    filteredActivities.addAll(activityDataList)

                    activityAdapter.notifyDataSetChanged()


                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error -> println(error.toString()) })


        queue.add(multipartResponseRequest)
    }

    private fun applyFilter(category: String) {

        /*val newList = activityDataList.filter { activity ->
            // For example, if you store a "category" or "type" field:
            // activity.type == category
            activity.type.contains(category, ignoreCase = true)
        }*/

        val allActivitiesSavedState = activityDataList

        activityDataList.sortBy { activity ->

            when(category.lowercase()){
                "sdg" -> return@sortBy activity.ods
                "country" -> return@sortBy activity.country
                else -> return@sortBy activity.type
            }

        }

        // Update the filtered list
        filteredActivities.clear()
        filteredActivities.addAll(activityDataList)


        activityDataList = allActivitiesSavedState
        // Notify adapter
        activityAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {

        val intent : Intent = Intent(this, ActivityDetailsActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("jwt_token", jwt_token)
        intent.putExtra("is_admin", is_admin)
        intent.putExtra("activity_id", filteredActivities[position].id)

        startActivity(intent)

        println("teste")

    }


}