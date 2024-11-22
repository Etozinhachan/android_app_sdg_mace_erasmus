package me.erasmusteam.odsmaceerasmusapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TemporaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temporary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bundle : Bundle? = intent.extras
        val jwt_token : String? = bundle?.getString("jwt_token")
        val is_admin : String? = bundle?.getString("is_admin")

        val project_btn : Button = findViewById(R.id.ProjectPage)
        val ods_btn : Button = findViewById(R.id.OdsPage)
        val mobilities_btn : Button = findViewById(R.id.MobilitiesPage)
        val activities_btn : Button = findViewById(R.id.ActivitiesPage)
        val settings_btn : Button = findViewById(R.id.SettingsPage)

        project_btn.setOnClickListener(){
            val intent : Intent = Intent(this, ProjectsActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)
            intent.putExtra("is_admin", is_admin)

            startActivity(intent)
        }

        ods_btn.setOnClickListener(){
            val intent : Intent = Intent(this, OdsActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)
            intent.putExtra("is_admin", is_admin)

            startActivity(intent)
        }

        mobilities_btn.setOnClickListener(){
            val intent : Intent = Intent(this, MobilitiesActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)
            intent.putExtra("is_admin", is_admin)

            startActivity(intent)
        }

        activities_btn.setOnClickListener(){
            val intent : Intent = Intent(this, ActivitiesActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)
            intent.putExtra("is_admin", is_admin)

            startActivity(intent)
        }

        settings_btn.setOnClickListener(){
            val intent : Intent = Intent(this, SettingsActivity::class.java)

            intent.putExtra("jwt_token", jwt_token)
            intent.putExtra("is_admin", is_admin)

            startActivity(intent)
        }

    }
}