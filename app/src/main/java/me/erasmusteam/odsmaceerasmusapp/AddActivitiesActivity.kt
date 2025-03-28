package me.erasmusteam.odsmaceerasmusapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.filters.DecimalRangeInputFilter
import me.erasmusteam.odsmaceerasmusapp.requests.MultipartRequest
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class AddActivitiesActivity : AppCompatActivity() {

    private lateinit var newImagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var legacyImagePickerLauncher: ActivityResultLauncher<String>
    private val PERMISSION_REQUEST_CODE = 1001


    private val selectedByteArrayImages = mutableListOf<ByteArray>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_activities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            newImagePickerLauncher = registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia()
            ) { uris: List<Uri>? ->
                selectedByteArrayImages.clear()

                if (!uris.isNullOrEmpty()) {
                    val limit = minOf(uris.size, 5)
                    var processedImages = 0

                    for (i in 0 until limit) {
                        processSelectedImageCompressingGlideAsync(this@AddActivitiesActivity, uris[i]) { byteArray ->
                            if (byteArray != null){
                                selectedByteArrayImages.add(byteArray)
                            }
                            processedImages++

                            if (processedImages == limit){
                                Toast.makeText(this@AddActivitiesActivity, "All images processed :)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                println("Selected Images: $selectedByteArrayImages")
            }
        }

        // For legacy devices (Android 11 and below, for example)
        legacyImagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris: List<Uri>? ->
            selectedByteArrayImages.clear()
            if (!uris.isNullOrEmpty()) {
                val limit = minOf(uris.size, 5)
                var processedImages = 0
                for (i in 0 until limit) {
                    processSelectedImageCompressingGlideAsync(this@AddActivitiesActivity, uris[i]) { byteArray ->
                        if (byteArray != null){
                            selectedByteArrayImages.add(byteArray)
                        }
                        processedImages++

                        if (processedImages == limit){
                            Toast.makeText(this@AddActivitiesActivity, "All images processed :)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            println("Selected Images: $selectedByteArrayImages")
        }

        val bundle: Bundle? = intent.extras
        val jwt_token: String? = bundle?.getString("jwt_token")

        val countryEditText: Spinner = findViewById(R.id.CountryEditText)
        val odsDropDown: Spinner = findViewById(R.id.OdsEditText)
        val typeEditText: EditText = findViewById(R.id.typeEditText)
        val explanationEditText: EditText = findViewById(R.id.explanationEditText)
        val latitudeEditText: EditText = findViewById(R.id.latitudeEditText)
        val longitudeEditText: EditText = findViewById(R.id.longitudeEditText)

        latitudeEditText.filters = arrayOf(DecimalRangeInputFilter(-90.0, 90.0))
        longitudeEditText.filters = arrayOf(DecimalRangeInputFilter(-180.0, 180.0))

        val imagePicker_btn: Button = findViewById(R.id.imagePickerBtn)
        val save_btn: Button = findViewById(R.id.SaveButton)
        val back_btn: Button = findViewById(R.id.BackButton)

        imagePicker_btn.setOnClickListener() {
            checkAndRequestPermissionsToOpenPhotoPicker()
        }

        save_btn.setOnClickListener() {

            if (selectedByteArrayImages.isEmpty()){
                Toast.makeText(this@AddActivitiesActivity, "Please wait for the images to process!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val queue: RequestQueue = Volley.newRequestQueue(this)
            val url: String = "${ApiDetails.url}api/activity"
            val headers = mapOf("Authorization" to "Bearer $jwt_token")

            val jsonObject = JSONObject()

            jsonObject.put("country", countryEditText.selectedItem)
            jsonObject.put("latitude", latitudeEditText.text)
            jsonObject.put("longitude", longitudeEditText.text)
            jsonObject.put("ods", odsDropDown.selectedItem)
            jsonObject.put("type", typeEditText.text)
            jsonObject.put("explanation", explanationEditText.text)


            //jsonObject.put("image_list", JSONArray(selectedByteArrayImages))


            println(jsonObject)

            /*
            val json_request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    try {

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
            }*/

            val multipartRequest = MultipartRequest(
                url = url,
                headers = headers,
                jsonPart = jsonObject.toString(),
                files = selectedByteArrayImages,
                listener = { response ->

                    for(x in selectedByteArrayImages){
                        if (checkImgCorrupted(x)){
                            println("Imagem corrompida")
                            println("Imagem body used: ${String(x, Charsets.UTF_8)}")
                        }
                        println("Image size: ${x.size}")
                    }

                    finish()
                },
                errorListener = { error ->
                    println("Error: ${error.message}")
                    Toast.makeText(this@AddActivitiesActivity, "ERROR ON SUBMITTING ACTIVITY GRRRR", Toast.LENGTH_SHORT).show()
                }
            )


            queue.add(multipartRequest)
        }

        back_btn.setOnClickListener() {
            finish()
        }

    }

    fun checkImgCorrupted(byteArray: ByteArray): Boolean {
        var options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        if (options.outWidth == -1 || options.outHeight == -1) {
            return true
        }
        return false
    }


    fun openPhotoPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Build a request for the new photo picker (Android 13+)
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            newImagePickerLauncher.launch(request)
        } else {
            // Launch the legacy file picker
            legacyImagePickerLauncher.launch("image/*")
        }
    }

    fun processSelectedImage(uri: Uri): ByteArray? {

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }

            val imageBytes = byteArrayOutputStream.toByteArray()
            inputStream?.close()

            return imageBytes
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun processSelectedImageCompressing(uri: Uri): ByteArray? {

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val byteArrayOutputStream = ByteArrayOutputStream()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 55, byteArrayOutputStream)
            else
                bitmap.compress(Bitmap.CompressFormat.WEBP, 55, byteArrayOutputStream)


            val imageBytes = byteArrayOutputStream.toByteArray()

            return imageBytes
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun processSelectedImageCompressingGlide(context: Context, uri: Uri): ByteArray? {
        return try {
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Bitmap.CompressFormat.WEBP_LOSSY
            else
                Bitmap.CompressFormat.WEBP

            val quality = 55

            // Use Glide to resize and encode the image to a file
            val futureTarget = Glide.with(context)
                .asBitmap()
                .load(uri)
                .override(800, 800) // Resize
                .submit()

            val bitmap = futureTarget.get()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(format, quality, byteArrayOutputStream)
            val compressedBytes = byteArrayOutputStream.toByteArray()

            val ist = context.contentResolver.openInputStream(uri)

            val originalSize = ist?.available() ?: -1
            ist?.close()
            Log.i("ImageCompression", "Original size: $originalSize bytes")
            Log.i("ImageCompression", "Compressed size ($format, $quality%): ${compressedBytes.size} bytes")

            Glide.with(context).clear(futureTarget)

            compressedBytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun processSelectedImageCompressingGlideAsync(context: Context, uri: Uri, callback: (ByteArray?) -> Unit){

        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
        val quality = 55

        Glide.with(context)
            .asBitmap()
            .load(uri)
            .override(800, 800) // Resize
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resource.compress(format, quality, byteArrayOutputStream)
                    val compressedBytes = byteArrayOutputStream.toByteArray()

                    val ist = context.contentResolver.openInputStream(uri)
                    val originalSize = ist?.available() ?: -1
                    ist?.close()

                    Log.i("ImageCompression", "Original size: $originalSize bytes")
                    Log.i("ImageCompression", "Compressed size ($format, $quality%): ${compressedBytes.size} bytes")

                    callback(compressedBytes) // Send the result back
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    callback(null) // If Glide fails, return null
                }
            })
    }

    private fun checkImagePermissions() : Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }
    }


    private fun checkAndRequestPermissionsToOpenPhotoPicker(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("Permission", "Requesting READ_MEDIA_IMAGES")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                Log.i("Permission", "READ_MEDIA_IMAGES already granted")
                openPhotoPicker()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("Permission", "Requesting READ_EXTERNAL_STORAGE")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                Log.i("Permission", "READ_EXTERNAL_STORAGE already granted")
                openPhotoPicker()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can open the photo picker now.
                openPhotoPicker()
            } else {
                // Permission denied, inform the user or disable the feature.
                Toast.makeText(this, "Permission required to select images", Toast.LENGTH_SHORT).show()
            }
        }
    }



}