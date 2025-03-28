package me.erasmusteam.odsmaceerasmusapp.objects

import com.google.gson.Gson
import me.erasmusteam.odsmaceerasmusapp.R
import org.json.JSONObject

object LocalizationHandler {

    public const val DEFAULT_LANG_CODE = "en_us"

    /* for example,

    {
        "en_us": {
            "Login": {
                "WelcomeMessage": "meow"
            }
        }
    }


     */


    val idToStringMapper: Map<String, Map<Int, String>> = mapOf(
        "Settings" to mapOf(
            R.id.SettingsTxt to "Title",
            R.id.switchMode to "Dark mode switch text"
        ),
    )

    private var langs : MutableMap<String, MutableMap<String, MutableMap<String,String>>> = mutableMapOf()

    fun saveLocalization(json_string: String, languageCode: String){
        //val jsonObject : JSONObject = JSONObject(json_string)

        var map : MutableMap<String, MutableMap<String, String>> = mutableMapOf()
        //jsonObject.keys().forEach {
        //    val holder = jsonObject.getString(it)

        //    map[holder] = jsonObject.getString(it)
        //}

        map = Gson().fromJson<MutableMap<String, MutableMap<String, String>>>(json_string, MutableMap::class.java)

        // checks if default lang exists the langs map
        if (langs.containsKey(DEFAULT_LANG_CODE)){
            // loops over every key in the default lang's map and checks if the current lang's map
            // has that key, and if it doesn't it creates that said key in the current map
            // and makes the value be the value in the default lang's map,
            // basically making it so if a lang's map doesn't have a string for something
            // that value will be the same as in the default lang's map
            langs[DEFAULT_LANG_CODE]?.keys?.forEach { key_to_check ->
                map.keys.forEach{ holder ->
                    if (!map[holder]?.containsKey(key_to_check)!!){
                        map[holder]?.set(key_to_check, langs[DEFAULT_LANG_CODE]?.get(key_to_check).toString())
                    }
                }
            }
        }

        langs[languageCode] = map
    }

    fun returnLocalizationMap(languageCode: String) : Map<String, Map<String, String>>{
        val map_to_return : Map<String,Map<String, String>> = langs[languageCode]?.toMap() ?: emptyMap()

        return map_to_return
    }

    fun return_lang_codes_available() : List<String>{
        val listToReturn : List<String> = langs.keys.toList()

        return listToReturn
    }
}