package me.erasmusteam.odsmaceerasmusapp.interfaces

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.logging.Logger
import me.erasmusteam.odsmaceerasmusapp.data.SharedPreferenceManager
import me.erasmusteam.odsmaceerasmusapp.objects.ApiDetails
import me.erasmusteam.odsmaceerasmusapp.objects.LocalizationHandler
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


interface ITranslationHolder {
    fun updateLangUsed(context: Context, translationHolderActivity: AppCompatActivity, langCode: String, viewsToUpdateList: List<TextView>) : Boolean{

        val sharedPreferenceManager = SharedPreferenceManager(context)

        if (langCode == sharedPreferenceManager.preferredLang){
            return false
        }

        sharedPreferenceManager.preferredLang = langCode

        val callerActivityName = translationHolderActivity::class.java.name.replace(translationHolderActivity.packageName + ".", "").replace("Activity", "")

        println(callerActivityName)

        val localizationMap: Map<String, String> = LocalizationHandler.returnLocalizationMap(langCode)[callerActivityName]!!

        viewsToUpdateList.forEach{
            val translationKey = LocalizationHandler.idToStringMapper[callerActivityName]?.get(it.id)
            it.text = localizationMap[translationKey]
        }



        return true

    }

    fun forceUpdateLangUsed(context: Context, translationHolderActivity: AppCompatActivity, langCode: String, viewsToUpdateList: List<TextView>){

        val sharedPreferenceManager = SharedPreferenceManager(context)

        sharedPreferenceManager.preferredLang = langCode

        val callerActivityName = translationHolderActivity::class.java.name.replace(translationHolderActivity.packageName + ".", "").replace("Activity", "")

        println(callerActivityName)

        val localizationMap: Map<String, String> = LocalizationHandler.returnLocalizationMap(langCode)[callerActivityName]!!

        viewsToUpdateList.forEach{
            val translationKey = LocalizationHandler.idToStringMapper[callerActivityName]?.get(it.id)
            it.text = localizationMap[translationKey]
        }

    }



    fun getTranslationJsonFromAPI(context: Context, onResult: (String) -> Unit){

        val queue: RequestQueue = Volley.newRequestQueue(context)

        val url = "${ApiDetails.url}api/Translation/translation"

        val string_request: StringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                Log.i("yipeee", response)
                onResult(response + "meow")
            },
            { error ->

                Log.e("grrr", "$error")
            })


        queue.add(string_request)
    }

    /*fun getTranslationJsonFromAPI(context: Context, translationHolderActivity: AppCompatActivity): String{

        Thread {
            val queue: RequestQueue = Volley.newRequestQueue(context)

            val rqFuture : RequestFuture<String> = RequestFuture.newFuture()

            val url = "${ApiDetails.url}api/Translation/translation"
            var returnValue = ""

            val string_request2: StringRequest = StringRequest(Request.Method.GET, url,
                { response ->

                    Log.i("yipeee", response)
                },
                { error ->

                    Log.e("grrr", "$error")
                })

            val string_request: StringRequest = StringRequest(Request.Method.GET, url, rqFuture
            ) { error ->
                Log.e("grrrr", "$error")
            }

            /*string_request.setRetryPolicy(
                DefaultRetryPolicy(
                    30 * 1000,
                    2,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )*/

            queue.add(string_request)
            queue.add(string_request2)

            try {
                returnValue = rqFuture.get(10, TimeUnit.SECONDS)
                println("Meowzer: $returnValue")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: TimeoutException){
                e.printStackTrace()
            }

        }.start()

    }
    */
}