package me.erasmusteam.odsmaceerasmusapp

import android.app.usage.NetworkStats
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import me.erasmusteam.odsmaceerasmusapp.interfaces.ITranslationHolder
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity(), ITranslationHolder {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var testString = ""

        getTranslationJsonFromAPI(this) { result ->
            testString = result
            Log.i("Testing String", testString)


            val json_example : String = "{\"NavigationDrawer\": " +
                    "{" +
                    "\"nav_project_title\": \"Project\"," +
                    "\"nav_partners_title\": \"Partners\",\"nav_SDGs_title\": \"SDGs\"," +
                    "\"nav_mobilities_title\": \"Mobilities\",\"nav_activities_title\": \"Activities\"," +
                    "\"nav_settings_title\": \"Settings\",\"nav_login_page_title\": \"Login Page\"" +
                    "}, " +
                    /*
                                    "\"Login\": " +
                                    "{" +
                                    "\"\": \"\"}" +
                                    "}, " +
                     */
                    "\"Settings\": " +
                    "{" +
                    "\"Title\": \"Settings\", \"Dark mode switch text\": \"Switch to dark mode\"}" +
                    "}"
            LocalizationHandler.saveLocalization(json_example, LocalizationHandler.DEFAULT_LANG_CODE)

            val json_example_pt : String = "{\"NavigationDrawer\": " +
                    "{" +
                    "\"nav_project_title\": \"Projeto\"," +
                    "\"nav_partners_title\": \"Parceiros\",\"nav_SDGs_title\": \"ODS\"," +
                    "\"nav_mobilities_title\": \"Mobilidades\",\"nav_activities_title\": \"Atividades\"," +
                    "\"nav_settings_title\": \"Definições\",\"nav_login_page_title\": \"Página de Login\"" +
                    "}, " +
                    "\"Settings\": " +
                    "{" +
                    "\"Title\": \"Definições\", \"Dark mode switch text\": \"Mudar para tema noturno\"}" +
                    "}"
            LocalizationHandler.saveLocalization(json_example_pt, "pt_pt")

        }

        Log.i("Testing String", testString)



        val sign_in_btn : Button = findViewById(R.id.SignUpPageBtn)
        val continue_as_guest_btn : Button = findViewById(R.id.GuestBtn)
        val login_btn : Button = findViewById(R.id.LogInBtn)

        val username_input : EditText = findViewById(R.id.LogInUsernameInput)
        val password_input : EditText = findViewById(R.id.LogInPasswordInput)


        sign_in_btn.setOnClickListener(){

            val intent : Intent = Intent(this, SignInActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

        }


        login_btn.setOnClickListener(){

            if(!checkCredentialsClientSide()){
                return@setOnClickListener
            }

            val usernameOrEmail : String = username_input.text.toString()
            val password : String = password_input.text.toString()
            val isEmail : Boolean = Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()


            val queue : RequestQueue = Volley.newRequestQueue(this)
            val url : String ="${ApiDetails.url}api/Users/login"


            val jsonObject = JSONObject()

            println("MEOW: $usernameOrEmail")
            println("RAWR: $password")
            println("GRRR: $isEmail")

            jsonObject.put("userName", usernameOrEmail)
            jsonObject.put("passHash", password)
            //jsonObject.put("isEmail", isEmail)

            val json_request : JsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    try {

                        clearInputErrors()

                        val intent : Intent = Intent(this, ActivitiesActivity::class.java)

                        intent.putExtra("jwt_token", response.get("jwt_token").toString())
                        intent.putExtra("is_admin", response.get("isAdmin").toString().toBoolean())
                        intent.putExtra("username", response.get("username").toString())

                        startActivity(intent)
                    } catch (e: JSONException){
                        e.printStackTrace()
                    }
                },
                { error ->

                    if (error.networkResponse.statusCode == 404){
                        showError(username_input, "No userssss found with that username")
                    }

                    if (error.networkResponse.statusCode == 400){
                        val errorJsonMap = Gson().fromJson<Map<String, Any>>(String(error.networkResponse.data), Map::class.java)
                        showError(username_input, errorJsonMap["title"].toString())
                        showError(password_input, errorJsonMap["title"].toString())
                    }

                    //val errorJsonMap = Gson().fromJson<Map<String, Any>>(String(error.networkResponse.data), Map::class.java)

                    //Log.e("LoginError", errorJsonMap.toString())
                }
            )


            queue.add(json_request)

        }

        continue_as_guest_btn.setOnClickListener(){
            val intent : Intent = Intent(this, ActivitiesActivity::class.java)

            intent.putExtra("jwt_token", "guest")
            intent.putExtra("is_admin", "false")
            intent.putExtra("username", "Guest")

            startActivity(intent)
        }

    }

    private fun checkCredentialsClientSide(): Boolean{

        clearInputErrors()

        val usernameInput: EditText = findViewById(R.id.LogInUsernameInput)
        val passwordInput: EditText = findViewById(R.id.LogInPasswordInput)

        var returnValue = true

        if (usernameInput.text.isEmpty() || usernameInput.text.length < 7){
            showError(usernameInput, "Usernames must be atleast 7 characters long")
            returnValue = false
        }

        if (passwordInput.text.isEmpty() || passwordInput.text.length < 8){
            showError(passwordInput, "Passwords need to be atleast 8 characters long")
            returnValue = false
        }

        if (returnValue){
            clearInputErrors()
        }

        return returnValue
    }

    private fun showError(inputField: EditText, errorMsg: String): Unit{
        inputField.error = errorMsg
    }

    private fun clearInputErrors(){
        findViewById<EditText>(R.id.LogInUsernameInput).error = null
        findViewById<EditText>(R.id.LogInPasswordInput).error = null
    }

}