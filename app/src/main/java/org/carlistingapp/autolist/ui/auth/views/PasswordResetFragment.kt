package org.carlistingapp.autolist.ui.auth.views
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.AuthRepository
import org.carlistingapp.autolist.databinding.FragmentPasswordResetBinding
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModel
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.util.regex.Pattern
class PasswordResetFragment() : Fragment(), KodeinAware {
    override val kodein by kodein()
    private lateinit var binding : FragmentPasswordResetBinding
    private val api : ListingCarsAPI by instance()
    private val repository : AuthRepository by instance()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private val args : PasswordResetFragmentArgs by navArgs()
    private lateinit var token : String
    private lateinit var userId : String
    private val PASSWORD_PATTERN = Pattern.compile("^" +
            //"(?=.*[0-9])" +  /
            // /at least 1 digit
            // "(?=.*[a-z])" +       //at least 1 lower case letter
            //"(?=.*[A-Z])" +       //at least 1 upper case letter
            //"(?=.*[a-zA-Z])" +    //any letter
            //"(?=.*[@#$%^&+=])" +  //at least 1 special character
            //"(?=\\S+$)" +         //no white spaces
            ".{6,}" +               //at least 4 characters
            "$"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding =  DataBindingUtil.inflate(inflater,
           R.layout.fragment_password_reset, container, false)
        userId = args.userId.toString()
        token = args.token.toString()
        //Toast.makeText(requireContext(),"$userId, $token", Toast.LENGTH_LONG).show()

        binding.buttonPasswordRestore.setOnClickListener {
            passwordReset()
        }
        return binding.root
    }

    private fun passwordReset(){
        val password = binding.textViewPassword.text.toString().trim()
        val confirmPassword = binding.textViewConfirmPassword.text.toString().trim()

        binding.passwordTextInputLayout.error = null
        binding.confirmPasswordTextInputLayout.error = null

        if (!PASSWORD_PATTERN.matcher(password).matches()){
            binding.passwordTextInputLayout.error = "Password Should Have At Least 6 Characters!!"
            return
        }

        if (confirmPassword != password){
            binding.confirmPasswordTextInputLayout.error = "Passwords Do Not Match!!"
            return
        }

        viewModel = ViewModelProvider(this.requireActivity(),factory).get(AuthViewModel::class.java)
            binding.progressBar.visibility = View.VISIBLE
            viewModel.passwordReset(userId,password,token,requireActivity())
            viewModel.passwordResetResponse.observe(viewLifecycleOwner, Observer {
                requireActivity().viewModelStore.clear()
                binding.progressBar.visibility = View.INVISIBLE
                binding.root.snackBar(it.message)
                if (it.message.indexOf("401") !=-1){
                    Toast.makeText(requireContext(),"Password Reset Token Expired or already used",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                if (it.message == "Your Password Has Been Reset Successfully, Please LogIn with your new password"){
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.LogInUserFragment)
                }
            })

    }
}