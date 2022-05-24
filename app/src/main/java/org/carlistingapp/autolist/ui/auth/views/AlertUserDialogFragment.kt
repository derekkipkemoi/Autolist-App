package org.carlistingapp.autolist.ui.auth.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.databinding.FragmentAlertUserDialogBinding

class AlertUserDialogFragment : DialogFragment() {

    private lateinit var binding : FragmentAlertUserDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_alert_user_dialog, container, false)
        val fromFragment = arguments?.getInt("FromFragment")
        val message = arguments?.getString("Message")

        binding.warningText.text = message
        binding.buttonTryAgain.setOnClickListener {
            when(fromFragment){
                1 -> {
                    val logInUserFragment = LogInUserFragment()
                    logInUserFragment.show(requireActivity().supportFragmentManager, "logInUserFragment")
                    dismiss()
                }
                2 -> {
                    val registerUserFragment = RegisterUserFragment()
                    registerUserFragment.show(requireActivity().supportFragmentManager, "registerUserFragment")
                    dismiss()
                }
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        return  binding.root
    }

    companion object {

    }
}