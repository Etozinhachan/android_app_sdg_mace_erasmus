package me.erasmusteam.odsmaceerasmusapp

import android.content.res.TypedArray
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.erasmusteam.odsmaceerasmusapp.data.ActivityData
import me.erasmusteam.odsmaceerasmusapp.data.ActivityDetailsStruct
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.requests.MultipartResponseRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EditActivitiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle : Bundle? = intent.extras
        val jwt_token : String? = bundle?.getString("jwt_token")
        val activity_id : String = bundle?.getString("activity_id")!!



        getActivityDetailsAndUpdateUI(jwt_token!!, activity_id)


        /*

        val countryEditText : Spinner = findViewById(R.id.CountryEditText)
        val odsDropDown : Spinner = findViewById(R.id.OdsEditText)
        val typeEditText : EditText = findViewById(R.id.typeEditText)
        val explanationEditText : EditText = findViewById(R.id.explanationEditText)
        val latitudeEditText : EditText = findViewById(R.id.latitudeEditText)
        val longitudeEditText : EditText = findViewById(R.id.longitudeEditText)

        val CountriesArray : TypedArray = resources.obtainTypedArray(R.array.Countries)
        for (i in 0 until CountriesArray.length()){
            if (CountriesArray.getString(i) == old_activity_details.country){
                countryEditText.setSelection(i)
                break
            }
        }
        CountriesArray.recycle()

        val SDGsArray : TypedArray = resources.obtainTypedArray(R.array.SDGs)
        for (i in 0 until SDGsArray.length()){
            if (SDGsArray.getString(i) == old_activity_details.ods){
                odsDropDown.setSelection(i)
                break
            }
        }
        SDGsArray.recycle()

        typeEditText.setText(old_activity_details.type)
        explanationEditText.setText(old_activity_details.explanation)
        latitudeEditText.setText(old_activity_details.latitude.toString())
        longitudeEditText.setText(old_activity_details.longitude.toString())

        val save_btn : Button = findViewById(R.id.SaveButton)
        val back_btn : Button = findViewById(R.id.BackButton)


        save_btn.setOnClickListener(){
            val queue : RequestQueue = Volley.newRequestQueue(this)
            val url : String ="${ApiDetails.url}api/admin/edit_activity/$activity_id"


            val jsonObject = JSONObject()



            jsonObject.put("country", countryEditText.selectedItem)
            jsonObject.put("latitude", latitudeEditText.text)
            jsonObject.put("longitude", longitudeEditText.text)
            jsonObject.put("ods", odsDropDown.selectedItem)
            jsonObject.put("type", typeEditText.text)
            jsonObject.put("explanation", explanationEditText.text)
            jsonObject.put("image_uris", JSONArray("[\"https://static.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg\"]"))

            val json_request : JsonObjectRequest = object: JsonObjectRequest(
                Request.Method.PATCH, url, jsonObject,
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

         */

    }

    private fun getActivityDetailsAndUpdateUI(jwt_token_param: String, activity_id: String){
        val queue : RequestQueue = Volley.newRequestQueue(this)
        val url : String ="${ApiDetails.url}api/Activity/${activity_id}"
        var old_activity_details : ActivityDetailsStruct? = null


        val multipartResponseRequest : MultipartResponseRequest = MultipartResponseRequest( url, mapOf("Authorization" to "Bearer $jwt_token_param"),
            { response ->
                try {

                    for (i in response.indices) {
                        var activity: ActivityData = response[i]
                        println(activity)
                        old_activity_details = ActivityDetailsStruct(
                            activity.images,
                            activity.id,
                            activity.country,
                            activity.latitude,
                            activity.longitude,
                            activity.ods,
                            activity.type,
                            activity.explanation
                        )
                    }

                    val countryEditText : Spinner = findViewById(R.id.CountryEditText)
                    val odsDropDown : Spinner = findViewById(R.id.OdsEditText)
                    val typeEditText : EditText = findViewById(R.id.typeEditText)
                    val explanationEditText : EditText = findViewById(R.id.explanationEditText)
                    val latitudeEditText : EditText = findViewById(R.id.latitudeEditText)
                    val longitudeEditText : EditText = findViewById(R.id.longitudeEditText)

                    val CountriesArray : TypedArray = resources.obtainTypedArray(R.array.Countries)
                    for (i in 0 until CountriesArray.length()){
                        if (CountriesArray.getString(i) == old_activity_details?.country){
                            countryEditText.setSelection(i)
                            break
                        }
                    }
                    CountriesArray.recycle()

                    val SDGsArray : TypedArray = resources.obtainTypedArray(R.array.SDGs)
                    for (i in 0 until SDGsArray.length()){
                        if (SDGsArray.getString(i) == old_activity_details?.ods){
                            odsDropDown.setSelection(i)
                            break
                        }
                    }
                    SDGsArray.recycle()

                    typeEditText.setText(old_activity_details?.type)
                    explanationEditText.setText(old_activity_details?.explanation)
                    latitudeEditText.setText(old_activity_details?.latitude.toString())
                    longitudeEditText.setText(old_activity_details?.longitude.toString())

                    val save_btn : Button = findViewById(R.id.SaveButton)
                    val back_btn : Button = findViewById(R.id.BackButton)


                    save_btn.setOnClickListener(){
                        val queue : RequestQueue = Volley.newRequestQueue(this)
                        val url : String ="${ApiDetails.url}api/admin/edit_activity/$activity_id"


                        val jsonObject = JSONObject()



                        jsonObject.put("country", countryEditText.selectedItem)
                        jsonObject.put("latitude", latitudeEditText.text)
                        jsonObject.put("longitude", longitudeEditText.text)
                        jsonObject.put("ods", odsDropDown.selectedItem)
                        jsonObject.put("type", typeEditText.text)
                        jsonObject.put("explanation", explanationEditText.text)
                        jsonObject.put("image_uris", JSONArray("[\"https://static.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg\"]"))

                        val json_request : JsonObjectRequest = object: JsonObjectRequest(
                            Method.PATCH, url, jsonObject,
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

                                headers["Authorization"] = "Bearer $jwt_token_param"

                                return headers
                            }
                        }


                        queue.add(json_request)
                    }

                    back_btn.setOnClickListener(){
                        finish()
                    }


                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })


        queue.add(multipartResponseRequest)
    }
}