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
import org.json.JSONException
import org.json.JSONObject

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
        val password_input : EditText = findViewById(R.id.SignUpPasswordInput)

        sign_in_btn.setOnClickListener(){

            val username : String = username_input.text.toString()
            val password : String = password_input.text.toString()

            val queue : RequestQueue = Volley.newRequestQueue(this)
            val url : String ="${ApiDetails.url}api/Users/register"


            val jsonObject = JSONObject()

            println("MEOW: $username")
            println("RAWR: $password")

            jsonObject.put("userName", username)
            jsonObject.put("passHash", password)

            val json_request : JsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    try {

                        val intent : Intent = Intent(this, ProjectsActivity::class.java)

                        intent.putExtra("jwt_token", response.get("jwt_token").toString())
                        intent.putExtra("is_admin", response.get("isAdmin").toString())
                        intent.putExtra("username", response.get("username").toString())

                        startActivity(intent)
                    } catch (e: JSONException){
                        e.printStackTrace()
                    }
                },
                { error -> /*Toast.makeText(this@MainActivity, "" + error.toString(), Toast.LENGTH_LONG).show()*/println(error.toString()) })


            queue.add(json_request)
        }

        login_btn.setOnClickListener(){
            finish()
        }

    }
}