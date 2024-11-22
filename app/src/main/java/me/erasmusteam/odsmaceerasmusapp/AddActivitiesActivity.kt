package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddActivitiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_activities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle : Bundle? = intent.extras
        val jwt_token : String? = bundle?.getString("jwt_token")

        val countryEditText : EditText = findViewById(R.id.CountryEditText)
        val odsEditText : EditText = findViewById(R.id.OdsEditText)
        val typeEditText : EditText = findViewById(R.id.typeEditText)
        val explanationEditText : EditText = findViewById(R.id.explanationEditText)
        val latitudeEditText : EditText = findViewById(R.id.latitudeEditText)
        val longitudeEditText : EditText = findViewById(R.id.longitudeEditText)

        val save_btn : Button = findViewById(R.id.SaveButton)
        val back_btn : Button = findViewById(R.id.BackButton)


        save_btn.setOnClickListener(){
            val queue : RequestQueue = Volley.newRequestQueue(this)
            val url : String ="${ApiDetails.url}api/activity"


            val jsonObject = JSONObject()



            jsonObject.put("country", countryEditText.text)
            jsonObject.put("latitude", latitudeEditText.text)
            jsonObject.put("longitude", longitudeEditText.text)
            jsonObject.put("ods", odsEditText.text)
            jsonObject.put("type", typeEditText.text)
            jsonObject.put("explanation", explanationEditText.text)
            jsonObject.put("image_uris", JSONArray("[\"https://static.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg\"]"))

            val json_request : JsonObjectRequest = object: JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    try {

                        finish()

                    } catch (e: JSONException){
                        e.printStackTrace()
                    }
                },
                { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers : MutableMap<String, String> = mutableMapOf()

                    headers["Authorization"] = "Bearer $jwt_token"

                    return headers
                }
            }


            queue.add(json_request)
        }

        back_btn.setOnClickListener(){
            finish()
        }

    }
}