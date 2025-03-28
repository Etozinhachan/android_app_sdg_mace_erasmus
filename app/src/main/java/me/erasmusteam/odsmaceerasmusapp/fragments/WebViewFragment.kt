package me.erasmusteam.odsmaceerasmusapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import me.erasmusteam.odsmaceerasmusapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "website_url"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var website_url: String? = null
    //private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            website_url = it.getString(ARG_PARAM1)
           // param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(website_url!!)

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(web: WebView, url: String) {
                web.loadUrl("javascript:(function(){document.getElementById('s9iPrd').style.visibility = 'hidden';document.querySelector('.GAuSPc').style.visibility = 'hidden';document.querySelector('.RBEWZc').style.visibility = 'hidden';})()")
            }
        })
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param website_url The url of the website to show in the fragment.
         * @return A new instance of fragment WebViewFragment.
         */
        @JvmStatic
        fun newInstance(website_url: String/*, param2: String*/) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, website_url)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}