package me.erasmusteam.odsmaceerasmusapp.interfaces

import android.content.Context
import android.content.Intent
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import me.erasmusteam.odsmaceerasmusapp.ActivitiesActivity
import me.erasmusteam.odsmaceerasmusapp.MainActivity
import me.erasmusteam.odsmaceerasmusapp.MobilitiesActivity
import me.erasmusteam.odsmaceerasmusapp.OdsActivity
import me.erasmusteam.odsmaceerasmusapp.PartnersActivity
import me.erasmusteam.odsmaceerasmusapp.ProjectsActivity
import me.erasmusteam.odsmaceerasmusapp.R
import me.erasmusteam.odsmaceerasmusapp.SettingsActivity
import me.erasmusteam.odsmaceerasmusapp.adapters.ExpandableListAdapter
import me.erasmusteam.odsmaceerasmusapp.data.SharedPreferenceManager
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler

interface IExpandableListHolder {

    fun setupExpandableListView(context: Context, expandableListHolderActivity: AppCompatActivity, jwt_token: String, is_admin: Boolean, header_username: String){

        val expandableListView: ExpandableListView = expandableListHolderActivity.findViewById(R.id.expandableListView)

        //val sharedPref = expandableListHolderActivity.getPreferences(Context.MODE_PRIVATE)
        //val lang_to_use : String = sharedPref.getString("preferred_lang", LocalizationHandler.DEFAULT_LANG_CODE).toString()

        val lang_to_use : String = SharedPreferenceManager(context).preferredLang.toString()

        val localization_map: Map<String, String> = LocalizationHandler.returnLocalizationMap(lang_to_use)["NavigationDrawer"]!!

        val project_title = localization_map["nav_project_title"]!!
        val partners_title = localization_map["nav_partners_title"]!!
        val sdgs_title = localization_map["nav_SDGs_title"]!!
        val mobilities_title = localization_map["nav_mobilities_title"]!!
        val activities_title = localization_map["nav_activities_title"]!!
        val settings_title = localization_map["nav_settings_title"]!!
        val login_page_title = localization_map["nav_login_page_title"]!!

        val mobilities = listOf("ES- Barcelona", "ES- Los Cardones, Tenerife", "PL- Kraków", "SE- Linköping")
        val mobilityEndpoints = hashMapOf(
            mobilities[0] to "es-barcelona",
            mobilities[1] to "es-los-cardones-tenerife",
            mobilities[2] to "pl-kraków",
            mobilities[3] to "se-linköping"
        )

        val headers = listOf(project_title, partners_title, sdgs_title, mobilities_title, activities_title, settings_title, login_page_title)

        val children = hashMapOf(
            project_title to emptyList<String>(),
            partners_title to emptyList<String>(),
            sdgs_title to emptyList<String>(),
            mobilities_title to mobilities,
            activities_title to emptyList<String>(),
            settings_title to emptyList<String>(),
            login_page_title to emptyList<String>()
        )

        val icons = hashMapOf(
            project_title to R.drawable.baseline_home_24,
            settings_title to R.drawable.baseline_settings_24,
            login_page_title to R.drawable.baseline_login_24
        )

        val adapter : ExpandableListAdapter = ExpandableListAdapter(context, headers, children, icons)
        expandableListView.setAdapter(adapter)

        expandableListView.setOnGroupClickListener { _, _, groupPosition, _ ->
            val isExpandable: Boolean = adapter.getChildrenCount(groupPosition) > 0

            val selectedItem = headers[groupPosition]
            var intent: Intent? = null


            when (selectedItem) {

                project_title -> {

                    val javaClassToCheck = ProjectsActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {
                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                        intent.putExtra("jwt_token", jwt_token)
                        intent.putExtra("is_admin", is_admin)
                        intent.putExtra("username", header_username)

                    }

                }

                partners_title -> {
                    val javaClassToCheck = PartnersActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {
                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                        intent.putExtra("jwt_token", jwt_token)
                        intent.putExtra("is_admin", is_admin)
                        intent.putExtra("username", header_username)
                    }
                }

                sdgs_title -> {

                    val javaClassToCheck = OdsActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {
                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                        intent.putExtra("jwt_token", jwt_token)
                        intent.putExtra("is_admin", is_admin)
                        intent.putExtra("username", header_username)

                    }
                }

                activities_title -> {

                    val javaClassToCheck = ActivitiesActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {

                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("jwt_token", jwt_token)
                        intent.putExtra("is_admin", is_admin)
                        intent.putExtra("username", header_username)

                    }

                }

                settings_title -> {

                    val javaClassToCheck = SettingsActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {

                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("jwt_token", jwt_token)
                        intent.putExtra("is_admin", is_admin)
                        intent.putExtra("username", header_username)

                    }

                }

                login_page_title -> {

                    val javaClassToCheck = MainActivity::class.java

                    if (expandableListHolderActivity::class.java.name != javaClassToCheck.name) {

                        intent = Intent(context, javaClassToCheck)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                    }

                }


            }

            expandableListView.post {
                if (intent != null){
                    startActivity(context, intent, null)
                }
            }

            !isExpandable
        }

        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->

            val selectedItem = children[headers[groupPosition]]?.get(childPosition)

            if (selectedItem in mobilities){

                if(expandableListHolderActivity::class.java.name == MobilitiesActivity::class.java.name){

                    if(expandableListHolderActivity.findViewById<TextView>(R.id.mobilitiesTxt).text == selectedItem){

                        return@setOnChildClickListener true
                    }
                }



                val intent: Intent = Intent(context, MobilitiesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                intent.putExtra("mobility_endpoint", mobilityEndpoints[selectedItem])
                intent.putExtra("mobility_title", selectedItem)

                intent.putExtra("jwt_token", jwt_token)
                intent.putExtra("is_admin", is_admin)
                intent.putExtra("username", header_username)

                expandableListView.post {
                    startActivity(context, intent, null)
                }

            }


            //Toast.makeText(expandableListHolderActivity, selectedItem, Toast.LENGTH_SHORT).show()
            true

        }
    }

}