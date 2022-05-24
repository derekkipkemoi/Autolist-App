package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.UserObject
import org.carlistingapp.autolist.databinding.FragmentEditUserPhoneBinding
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditUserPhoneFragment : DialogFragment(), KodeinAware {
    override val kodein by kodein()
    val session : Session by instance()

    private lateinit var binding : FragmentEditUserPhoneBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_user_phone, container, false)
        val view = binding.root
        val user = session.get<UserObject>("UserObject")
        binding.textViewUserPhoneNumber.text = "0"+user?.phoneNumber?.number?.toString()
        binding.textViewPhoneVerified.text = "You Have Successfully Verified Your Number"

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonUpdate.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.phoneNumberFragment)
        }
        return  view
    }

}

