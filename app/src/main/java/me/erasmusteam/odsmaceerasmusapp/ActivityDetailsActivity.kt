package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import me.erasmusteam.odsmaceerasmusapp.data.ActivityData
import me.erasmusteam.odsmaceerasmusapp.data.ActivityDetailsStruct
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.requests.MultipartResponseRequest
import me.erasmusteam.odsmaceerasmusapp.fragments.MapFragment
import me.erasmusteam.odsmaceerasmusapp.interfaces.IFragmentHolder
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ActivityDetailsActivity : AppCompatActivity(), IFragmentHolder {

    private lateinit var imageList: ArrayList<SlideModel>
    private lateinit var jwt_token: String
    private lateinit var activity_id: String


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle : Bundle? = intent.extras
        jwt_token = bundle?.getString("jwt_token") ?: ""
        val is_admin = bundle?.getBoolean("is_admin")!!
        activity_id = bundle.getString("activity_id") ?: ""
        /*val old_activity_details_str = bundle?.getString("activity_details")

        var images : MutableList<ByteArray> = mutableListOf()

        for(i in 0 until 5){
            val t = bundle?.getByteArray("activity_images${i}")

            if (t != null){
                images.add(t)
            }

        }

        val split_old_activity_details_str = old_activity_details_str!!.split("!.!")

        val old_activity_details: ActivityDetailsStruct = ActivityDetailsStruct(images, split_old_activity_details_str[6], split_old_activity_details_str[0], split_old_activity_details_str[1].toDouble(), split_old_activity_details_str[2].toDouble(), split_old_activity_details_str[3], split_old_activity_details_str[4], split_old_activity_details_str[5])

        println("Activity Details: ${old_activity_details}")*/

        val back_btn : Button = findViewById(R.id.BackButton)

        back_btn.text = "Back"
        back_btn.setBackgroundColor(getColor(R.color.red))

        back_btn.setOnClickListener(){
            finish()
        }

        getActivityAndUpdateDetails(jwt_token)
        if (is_admin){

            val btnLayout : LinearLayout = findViewById(R.id.buttonLayout)
            val btnLayout2: LinearLayout = findViewById(R.id.buttonLayout2)

            val inflater: LayoutInflater = LayoutInflater.from(this@ActivityDetailsActivity)

            val acceptBtn: Button = inflater.inflate(R.layout.activity_details_button, btnLayout, false) as Button
            acceptBtn.text = "Accept"
            acceptBtn.setBackgroundColor(getColor(R.color.green))
            val rejectBtn: Button = inflater.inflate(R.layout.activity_details_button, btnLayout, false) as Button
            rejectBtn.text = "Reject"
            rejectBtn.setBackgroundColor(getColor(R.color.red))
            val editBtn: Button = inflater.inflate(R.layout.activity_details_button, btnLayout2, false) as Button
            editBtn.text = "Edit"
            editBtn.setBackgroundColor(getColor(R.color.blue))

            /*val editBtn = Button(this)

            val acceptBtn = Button(this)
            val rejectBtn = Button(this)

            val btnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            editBtn.text = "Edit"
            acceptBtn.text = "Accept"
            rejectBtn.text = "Reject"

            editBtn.setBackgroundColor(getColor(R.color.blue))
            acceptBtn.setBackgroundColor(getColor(R.color.green))
            rejectBtn.setBackgroundColor(getColor(R.color.red))


            editBtn.layoutParams = btnParams
            acceptBtn.layoutParams = btnParams
            rejectBtn.layoutParams = btnParams


            editBtn.requestLayout()
            acceptBtn.requestLayout()
            rejectBtn.requestLayout()*/

            editBtn.setOnClickListener() {
                val intent : Intent = Intent(this, EditActivitiesActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("jwt_token", jwt_token)
                intent.putExtra("activity_id", activity_id)

                startActivity(intent)
            }

            val queue_2: RequestQueue = Volley.newRequestQueue(this)


            acceptBtn.setOnClickListener(){

                var url : String = ""
                var word : String = ""
                url = "${ApiDetails.url}api/admin/accept_activity/${activity_id}"

                 val string_request: StringRequest = object : StringRequest(
                     Request.Method.GET, url,
                     { response ->
                         try {

                             Toast.makeText(
                                 this@ActivityDetailsActivity,
                                 "Accepted Activity!",
                                 Toast.LENGTH_SHORT
                             ).show()

                             finish()


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


                finish()
            }

            rejectBtn.setOnClickListener(){

                var url : String = ""
                var word : String = ""
                url = "${ApiDetails.url}api/admin/reject_activity/${activity_id}"

                val string_request: StringRequest = object : StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {

                            Toast.makeText(
                                this@ActivityDetailsActivity,
                                "Rejected Activity!",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()


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


                finish()
            }

            btnLayout.addView(editBtn)
            btnLayout2.addView(acceptBtn)
            btnLayout2.addView(rejectBtn)

        }

    }

    override fun onRestart() {
        super.onRestart()

        val countryValue : TextView = findViewById(R.id.CountryValue)
        val SDGValue : TextView = findViewById(R.id.SDGValue)
        val typeValue : TextView = findViewById(R.id.TypeValue)
        val longitudeValue : TextView = findViewById(R.id.LongitudeValue)
        val latitudeValue : TextView = findViewById(R.id.LatitudeValue)
        val descriptionValue: TextView = findViewById(R.id.DescriptionValue)
        val imageSlider : ImageSlider = findViewById(R.id.imageSlider)

        countryValue.text = ""
        SDGValue.text = ""
        typeValue.text = ""
        longitudeValue.text = ""
        latitudeValue.text = ""
        descriptionValue.text = ""

        imageSlider.setImageList(emptyList())

        getActivityAndUpdateDetails(jwt_token)
    }

    private fun doFunStuff(jwt_token: String?, activity: ActivityDetailsStruct) {

       /* val queue : RequestQueue = Volley.newRequestQueue(this)
        val url : String ="${ApiDetails.url}api/Activity/${activity_id}"

        val json_request : JsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    println(response)

                    val image_list = response.getJSONArray("image_list")

                    println(image_list.length())

                    for(j in 0 until image_list.length()) {

                        val image_class = image_list.getJSONObject(j)

                        println(image_class.getString("id"))

                        val base64Image = image_class.getString("image")

                        decodeAndSaveImage(base64Image, activity_id, j)

                    }


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


        queue.add(json_request)*/

        val image_list = activity.image_uris

        println(image_list!!.size)

        println("Image array: ${image_list}")

        val imageSlider : ImageSlider = findViewById(R.id.imageSlider)

        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
        val quality = 55

        for(j in 0 until image_list.size) {

            println("Image: ${image_list[j]}")

            Glide.with(imageSlider.context)
                .asBitmap()
                .load(image_list[j])
                .override(imageSlider.width, imageSlider.height) // Resize before displaying
                .into(object: CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        resource.compress(format, quality, byteArrayOutputStream)
                        val compressedBytes = byteArrayOutputStream.toByteArray()


                        Log.i("ImageSizeBeforeCompression", image_list[j].size.toString())
                        Log.i("ImageSizeAfterCompression", compressedBytes.size.toString())
                        decodeAndSaveImage(compressedBytes, activity.activity_id, j)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })


        }


    }


    private fun decodeAndSaveImage(byteArrayImage: ByteArray, idx1: String?, idx2: Int){
        try{
            //val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP

            val bitmap = BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.size)

            val tempFile = File(cacheDir, "temp${idx1}m${idx2}.jpg")

            try {
                val fos = FileOutputStream(tempFile)
                bitmap.compress(format, 100, fos)
                fos.close()
            } catch (e: IOException) {
                // Handle error
            }
            val uri = Uri.fromFile(tempFile)

            imageList.add(SlideModel(uri.toString()))

            val imageSlider : ImageSlider = findViewById(R.id.imageSlider)
            imageSlider.setImageList(imageList)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getActivityAndUpdateDetails(jwt_token_param: String?){
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

                    val countryValue : TextView = findViewById(R.id.CountryValue)
                    val SDGValue : TextView = findViewById(R.id.SDGValue)
                    val typeValue : TextView = findViewById(R.id.TypeValue)
                    val longitudeValue : TextView = findViewById(R.id.LongitudeValue)
                    val latitudeValue : TextView = findViewById(R.id.LatitudeValue)
                    val descriptionValue: TextView = findViewById(R.id.DescriptionValue)

                    countryValue.text = old_activity_details?.country ?: ""
                    SDGValue.text = old_activity_details?.ods ?: ""
                    typeValue.text = old_activity_details?.type ?: ""
                    longitudeValue.text = old_activity_details?.longitude.toString()
                    latitudeValue.text = old_activity_details?.latitude.toString()
                    descriptionValue.text = old_activity_details?.explanation

                    imageList = ArrayList<SlideModel>()

                    val fragmentToBeAdded : MapFragment = MapFragment.newInstance(old_activity_details!!.latitude, old_activity_details!!.longitude)
                    addFragmentToActivity(R.id.fragment_container_view, fragmentToBeAdded, supportFragmentManager)

                    doFunStuff(jwt_token_param, old_activity_details!!)

                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })


        queue.add(multipartResponseRequest)
    }
}