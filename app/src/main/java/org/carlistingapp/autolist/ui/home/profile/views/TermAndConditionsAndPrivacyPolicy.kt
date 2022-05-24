package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.databinding.FragmentTermAndConditionsAndPrivacyPolicyBinding


class TermAndConditionsAndPrivacyPolicy : Fragment() {
    private lateinit var binding : FragmentTermAndConditionsAndPrivacyPolicyBinding
    private lateinit var url : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("Url").toString()
        //Toast.makeText(requireActivity(), url, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding =  DataBindingUtil.inflate(
           inflater,
           R.layout.fragment_term_and_conditions_and_privacy_policy,
           container,
           false
       )
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = binding.webView
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true;
        webSettings.useWideViewPort = true;
        webSettings.loadWithOverviewMode = true;

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.INVISIBLE
            }
        }


        webView.loadUrl(url)
    }

}