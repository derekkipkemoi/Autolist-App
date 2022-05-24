package org.carlistingapp.autolist.ui.auth.views

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.AuthRepository
import org.carlistingapp.autolist.databinding.FragmentForgotPasswordBinding
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModel
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ForgotPasswordFragment() : BottomSheetDialogFragment(), KodeinAware {
    override val kodein by kodein()
    private val api : ListingCarsAPI by instance()
    private val repository : AuthRepository by instance()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding : FragmentForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password, container, false)
        binding.buttonPasswordRestore.setOnClickListener {
            requestPasswordResetLink()
        }

        return binding.root
    }

    private fun requestPasswordResetLink() {
        val email = binding.textViewEmail.text.toString().trim()
        binding.emailTextInputLayout.error = null
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailTextInputLayout.error = "Valid Email Address Required!!"
            return
        }

        viewModel = ViewModelProvider(this.requireActivity(),factory).get(AuthViewModel::class.java)

        binding.progressBar.visibility = View.VISIBLE
        viewModel.requestPasswordResetLink(email, requireContext())
        viewModel.passwordResetResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visibility = View.INVISIBLE
            binding.root.snackBar(it.message)
            if (it.message == "Password reset link has been sent to $email please use it to reset your password"){
                findNavController().popBackStack()
                findNavController().navigate(R.id.passwordResetLinkSentFragment)
            }
            requireActivity().viewModelStore.clear()
        })



    }


}