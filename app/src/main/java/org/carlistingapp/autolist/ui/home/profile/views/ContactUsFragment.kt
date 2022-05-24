package org.carlistingapp.autolist.ui.home.profile.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.ContactUs
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.databinding.FragmentContactUsBinding
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.net.URLEncoder


class ContactUsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val factory: UserViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentContactUsBinding

    private lateinit var userId : String
    private lateinit var email : String
    private lateinit var subject : String
    private lateinit var userMessage : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate( inflater, R.layout.fragment_contact_us, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!session.getUserEmail().isNullOrEmpty()){
            email = session.getUserEmail().toString()
            binding.textViewFrom.text = Editable.Factory.getInstance().newEditable(
                email
            )
        }

        binding.buttonCall.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode("0115539223"))
            )
            ContextCompat.startActivity(requireActivity(), intent, null)
        }

        binding.buttonChat.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=" + "254115539223" + "&text=" + URLEncoder.encode(
                    "Hello...",
                    "UTF-8"
                )
            sendIntent.data = Uri.parse(url)
            requireActivity().startActivity(sendIntent)
        }

        binding.buttonSend.setOnClickListener {
            contactUs()
        }

    }

    private fun contactUs(){
        email = binding.textViewFrom.text.toString().trim()
        subject = binding.textViewSubject.text.toString().trim()
        userMessage = binding.textViewMessage.text.toString().trim()
        binding.fromTextInputLayout.error = null
        binding.subjectTextInputLayout.error = null
        binding.messageTextInputLayout.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.fromTextInputLayout.error = "Valid email address required!!"
            return
        }

        if (subject.length < 10){
            binding.subjectTextInputLayout.error = "Subject too short!!"
            return
        }

        if (userMessage.length < 30){
            binding.messageTextInputLayout.error = "Message too short!!"
            return
        }

        viewModel = ViewModelProvider(this,factory).get(UserViewModel::class.java)
        userId = session.getSession().toString()
        val contactUs = ContactUs(email, subject, userMessage)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.contactUs(userId,contactUs,requireActivity())

        viewModel.responseGeneralMessage.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visibility = View.GONE
            requireActivity().viewModelStore.clear()
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        })

    }

    companion object {
    }
}