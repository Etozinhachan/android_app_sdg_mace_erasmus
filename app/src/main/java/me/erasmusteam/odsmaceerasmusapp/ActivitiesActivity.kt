package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text

class ActivitiesActivity : AppCompatActivity() {

    var button_id_map : MutableMap<String, Int> = mutableMapOf()
    var activity_map : MutableMap<Int, JSONObject> = mutableMapOf()
    var jwt_token : String? = ""
    var header_username : String? = ""
    lateinit var toggle : ActionBarDrawerToggle

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
        val is_admin_str = bundle?.getString("is_admin")
        val is_admin : Boolean = is_admin_str.toBoolean()
        header_username = bundle?.getString("username")

        val scroll_view_activities : LinearLayout = findViewById(R.id.notimportantlayout)

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
                    intent.putExtra("is_admin", is_admin_str)
                    intent.putExtra("username", header_username)

                    startActivity(intent)

                }
                R.id.nav_ods -> {
                    val intent : Intent = Intent(this, OdsActivity::class.java)

                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin_str)
                    intent.putExtra("username", header_username)

                    startActivity(intent)
                }
                R.id.nav_mobilities -> {

                    val intent : Intent = Intent(this, MobilitiesActivity::class.java)

                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin_str)
                    intent.putExtra("username", header_username)

                    startActivity(intent)

                }
                R.id.nav_activities -> {}
                R.id.nav_settings -> {

                    val intent : Intent = Intent(this, SettingsActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("is_admin", is_admin_str)
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



        val addFAB : FloatingActionButton = findViewById(R.id.add_activity_button)

        addFAB.setOnClickListener(){
            val intent : Intent = Intent(this, AddActivitiesActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)

            startActivity(intent)
        }

        if (jwt_token == "guest"){
            addFAB.visibility = View.INVISIBLE
        }



        val queue : RequestQueue = Volley.newRequestQueue(this)
        val url : String ="${ApiDetails.url}api/Activity"

        button_id_map = mutableMapOf()
        activity_map = mutableMapOf()

        val json_request : JsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    var n_btn : Int = 0
                    println(response)

                    for (i in 0 until response.length()){
                        var activity : JSONObject = response.getJSONObject(i)
                            // here i gotta do somehting, i already have the activity, now i just
                            // need to list the activities making those lil thingies ik what im yapping abt
                            // tmrw ill try to do it but im eepy so ill just go to eep
                        if (!is_admin){
                            if (activity.getInt("activity_state") != 2) {
                                continue
                            }
                        }



                        val containerLayout : LinearLayout = LinearLayout(this)
                        val textContainerLayout : LinearLayout = LinearLayout(this)

                        if (activity.getInt("activity_state") == 1){
                            //println("GRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")
                            textContainerLayout.setBackgroundColor(Color.parseColor("#0099FF"))
                        }else if(activity.getInt("activity_state") == 0){
                            textContainerLayout.setBackgroundColor(Color.parseColor("#FF0000"))
                        }

                        val container_params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        val text_container_params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        containerLayout.layoutParams = container_params
                        textContainerLayout.layoutParams = text_container_params

                        containerLayout.orientation = LinearLayout.HORIZONTAL
                        textContainerLayout.orientation = LinearLayout.VERTICAL

                        val showImage : ImageView = ImageView(this)

                        showImage.setImageResource(R.drawable.esl_logo)



                        val activity_country : TextView = TextView(this)
                        activity_country.text = activity.getString("country")
                        val activity_ods : TextView = TextView(this)
                        activity_ods.text = activity.getString("ods")
                        val activity_type : TextView = TextView(this)
                        activity_type.text = activity.getString("type")
                        val activity_explanation : TextView = TextView(this)
                        activity_explanation.text = activity.getString("explanation")
                        val cool_params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        activity_country.layoutParams = cool_params
                        activity_ods.layoutParams = cool_params
                        activity_type.layoutParams = cool_params
                        activity_explanation.layoutParams = cool_params

                        textContainerLayout.addView(activity_country)
                        textContainerLayout.addView(activity_ods)
                        textContainerLayout.addView(activity_type)
                        textContainerLayout.addView(activity_explanation)

                        containerLayout.addView(showImage)
                        showImage.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics).toInt()
                        showImage.layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics).toInt()
                        showImage.requestLayout()

                        if (activity.getInt("activity_state") == 1){
                            val ButtonLayout : LinearLayout = LinearLayout(this)

                            val cool_layout_params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )

                            ButtonLayout.layoutParams = cool_layout_params
                            ButtonLayout.orientation = LinearLayout.VERTICAL

                            val accept_button : Button = Button(this)
                            val reject_button : Button = Button(this)
                            val edit_button : Button = Button(this)

                            ++n_btn

                            accept_button.setText("Accept")
                            reject_button.setText("Reject")
                            edit_button.setText("Edit")

                            val btn_params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                            accept_button.layoutParams = btn_params
                            reject_button.layoutParams = btn_params
                            edit_button.layoutParams = btn_params

                            accept_button.id = View.generateViewId()
                            reject_button.id = View.generateViewId()
                            edit_button.id = View.generateViewId()

                            button_id_map["accept_button${n_btn}"] = accept_button.id
                            button_id_map["reject_button${n_btn}"] = reject_button.id
                            button_id_map["edit_button${n_btn}"] = edit_button.id
                            activity_map[accept_button.id] = activity
                            activity_map[reject_button.id] = activity
                            activity_map[edit_button.id] = activity


                            ButtonLayout.addView(accept_button)
                            ButtonLayout.addView(reject_button)
                            ButtonLayout.addView(edit_button)
                            containerLayout.addView(ButtonLayout)

                        }

                        containerLayout.addView(textContainerLayout)




                        scroll_view_activities.addView(containerLayout)

                    }

                    connectButtons()
                    /*username_view.text = response.getString("userName")
                    id_view.text = response.getString("id")
                    is_admin_view.text = response.getBoolean("isAdmin").toString()
                    jwt_token_view.text = jwt_token*/


                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })


        queue.add(json_request)

    }

    private fun connectButtons(){
        println(button_id_map)
        println(activity_map)

        button_id_map.forEach() {
            val btn_id = it.value
            val isAcceptButton : Boolean = it.key.toString().contains("accept")
            val isEditButton : Boolean = it.key.contains("edit")

            println(btn_id)

            val btn: Button = findViewById(btn_id)



            btn.setOnClickListener() {

                val queue_2: RequestQueue = Volley.newRequestQueue(this)
                var url : String = ""
                var word : String = ""
                if (isAcceptButton){
                    word = "Accepted"
                    url = "${ApiDetails.url}api/admin/accept_activity/${activity_map[btn_id]?.getString("id")}"
                }else if(!isEditButton){
                    word = "Rejected"
                    url = "${ApiDetails.url}api/admin/reject_activity/${activity_map[btn_id]?.getString("id")}"
                }

                if (word == ""){
                    val intent : Intent = Intent(this, EditActivitiesActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("jwt_token", jwt_token)
                    intent.putExtra("activity_id", activity_map[btn_id]?.getString("id"))

                    startActivity(intent)
                }else{
                    val string_request: StringRequest = object : StringRequest(
                        Request.Method.GET, url,
                        { response ->
                            try {

                                Toast.makeText(
                                    this@ActivitiesActivity,
                                    "$word Activity!",
                                    Toast.LENGTH_SHORT
                                ).show()


                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        },
                        { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(
                            error.toString()
                        )
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers: MutableMap<String, String> = mutableMapOf()
                            headers["Authorization"] = "Bearer $jwt_token"
                            return headers
                        }
                    }


                    queue_2.add(string_request)
                }


            }
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