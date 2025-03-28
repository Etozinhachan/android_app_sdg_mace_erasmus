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
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sign_in_btn : Button = findViewById(R.id.SignUpBtn)
        val login_btn : Button = findViewById(R.id.loginPageBtn)

        val username_input : EditText = findViewById(R.id.SignUpUsernameInput)
        val email_input : EditText = findViewById(R.id.SignUpEmailnput)
        val password_input : EditText = findViewById(R.id.SignUpPasswordInput)

        sign_in_btn.setOnClickListener(){

            if (!checkCredentialsClientSide()){
                return@setOnClickListener
            }

            val username : String = username_input.text.toString()
            val password : String = password_input.text.toString()
            val email : String = email_input.text.toString()

            val queue : RequestQueue = Volley.newRequestQueue(this)
            val url : String ="${ApiDetails.url}api/Users/register"


            val jsonObject = JSONObject()

            println("MEOW: $username")
            println("RAWR: $password")

            jsonObject.put("userName", username)
            //jsonObject.put("email", email)
            jsonObject.put("passHash", password)
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

                    clearInputErrors()

                    showError(username_input, String(error.networkResponse.data))

                })



            queue.add(json_request)
        }

        login_btn.setOnClickListener(){
            finish()
        }

    }

    private fun checkCredentialsClientSide(): Boolean{

        clearInputErrors()

        val usernameInput: EditText = findViewById(R.id.SignUpUsernameInput)
        val emailInput: EditText = findViewById(R.id.SignUpEmailnput)
        val passwordInput: EditText = findViewById(R.id.SignUpPasswordInput)
        val confirmPasswordInput: EditText = findViewById(R.id.SignUpConfirmPasswordInput)

        var returnValue = true

        if (usernameInput.text.isEmpty() || usernameInput.text.length < 3){
            showError(usernameInput, "Username must have atleast 3 characters")
            returnValue = false
        }

        if (confirmPasswordInput.text.isEmpty() || confirmPasswordInput.text.toString() != passwordInput.text.toString()){
            showError(confirmPasswordInput, "The passwords don't match")
            showError(passwordInput, "The passwords don't match")
            returnValue = false
        }

        if (!isValidPassword(passwordInput.text.toString())){
            showError(passwordInput, "Password must have 1 capitalized letter, 1 number, 1 special character and 1 letter")
            returnValue = true
        }

        if (passwordInput.text.isEmpty() || passwordInput.text.length < 8){
            showError(passwordInput, "Password must have atleast 8 characters")
            returnValue = false
        }

        return returnValue
    }

    private fun showError(inputField: EditText, errorMsg: String): Unit{
        inputField.error = errorMsg
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)

        return matcher.matches()
    }

    private fun clearInputErrors(){
        findViewById<EditText>(R.id.SignUpUsernameInput).error = null
        findViewById<EditText>(R.id.SignUpPasswordInput).error = null
        findViewById<EditText>(R.id.SignUpConfirmPasswordInput).error = null
    }
}