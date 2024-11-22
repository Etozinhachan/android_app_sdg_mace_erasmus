package me.erasmusteam.odsmaceerasmusapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bundle : Bundle? = intent.extras
        val jwt_token : String? = bundle?.getString("jwt_token")

        val main_menu_btn : ImageButton = findViewById(R.id.menuImgBtn)

        main_menu_btn.setOnClickListener(){
            /*val intent : Intent = Intent(this, TemporaryActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)

            startActivity(intent)*/
            finish()
        }

        /*
        val username_view : TextView = findViewById(R.id.username_view)
        val id_view : TextView = findViewById(R.id.id_view)
        val is_admin_view : TextView = findViewById(R.id.is_admin_view)
        val jwt_token_view : TextView = findViewById(R.id.jwt_token_view)
        val return_btn : Button = findViewById(R.id.button)

        val queue : RequestQueue = Volley.newRequestQueue(this)
        val url : String ="https://api-ods-mace-erasmus.onrender.com/api/Users/id"

        val json_request : JsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {

                    response.keys().forEach(){
                        println(it)
                    }

                    username_view.text = response.getString("userName")
                    id_view.text = response.getString("id")
                    is_admin_view.text = response.getBoolean("isAdmin").toString()
                    jwt_token_view.text = jwt_token


                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $jwt_token"
                return headers
            }
        }


        queue.add(json_request)

        return_btn.setOnClickListener(){
            finish()
        }*/
    }
}