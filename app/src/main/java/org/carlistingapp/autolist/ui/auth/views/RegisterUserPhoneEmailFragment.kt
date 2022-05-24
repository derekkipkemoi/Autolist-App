package org.carlistingapp.autolist.ui.auth.views

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.User
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.AuthRepository
import org.carlistingapp.autolist.databinding.FragmentRegisterUserPhoneEmailBinding
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModel
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.utils.CustomAlertDialog
import org.carlistingapp.autolist.utils.Session
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterUserPhoneEmailFragment : BottomSheetDialogFragment(), KodeinAware {
    override val kodein by kodein()
    private val session : Session by instance()
    private val api : ListingCarsAPI by instance()
    private val repository : AuthRepository by instance()
    private val factory : AuthViewModelFactory by instance()


    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentRegisterUserPhoneEmailBinding

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register_user_phone_email, container, false)

        viewModel = ViewModelProvider(this.requireActivity(),factory).get(AuthViewModel::class.java)
        binding.buttonSignUp.setOnClickListener {
            singUpUser()
        }
        return binding.root
    }

    private fun singUpUser() {
        val firstName = binding.textViewFirstName.text.toString().trim()
        val lastName = binding.textViewLastName.text.toString().trim()
        val email = binding.textViewEmail.text.toString().trim()
        val password = binding.textViewPassword.text.toString().trim()
        val confirmPassword= binding.textViewConfirmPassword.text.toString().trim()

        binding.firstNameTextInputLayout.error = null
        binding.lastNameTextInputLayout.error = null
        binding.emailTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null
        binding.confirmPasswordTextInputLayout.error = null

        val regx = "^[A-Za-z\\s]+[.]?[A-Za-z\\s]{0,}\$"
        val pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE)
        val firstNameMatcher: Matcher = pattern.matcher(firstName)
        val lastNameMatcher: Matcher = pattern.matcher(lastName)

        if (!firstNameMatcher.matches()) {
            binding.firstNameTextInputLayout.error = "Valid first Name Required!!"
            return
        }
        if (!lastNameMatcher.matches()) {
            binding.lastNameTextInputLayout.error = "Valid last Name Required!!"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailTextInputLayout.error = "Valid Email Address Required!!"
            return
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()){
            binding.passwordTextInputLayout.error = "Password Should Have At Least 6 Characters!!"
            return
        }

        if (confirmPassword != password){
            binding.confirmPasswordTextInputLayout.error = "Passwords Do Not Match!!"
            return
        }



        val name = firstName.plus(" ").plus(lastName)

        binding.buttonSignUp.isEnabled = false
        val customAlertDialog = CustomAlertDialog(requireActivity())
        customAlertDialog.startLoadingDialog("Registering User")
        binding.progressBar.visibility = View.VISIBLE
        val usersObject = User(email,password,name)
        viewModel.getSignedUpUser(usersObject, requireContext())
            viewModel.registeredUserResponse.observe(viewLifecycleOwner, Observer {
                requireActivity().viewModelStore.clear()
                customAlertDialog.stopDialog()
                binding.progressBar.visibility = View.INVISIBLE
                if (it.access_token != null){
                    binding.root.snackBar(it.message)
                    session.put(it.userObject!!,"UserObject")
                    session.saveSession(it.userObject.id)
                    session.saveUserName(it.userObject.local?.name.toString())
                    session.saveUserEmail(it.userObject.local?.email.toString())
                    session.saveUserPicture(it.userObject.local?.picture.toString())
                    loadProfileActivity()

                }
                else{
                    binding.buttonSignUp.isEnabled = true
                   // binding.root.snackBar(it.message)

                    val alertUserDialogFragment = AlertUserDialogFragment()
                    val bundle = bundleOf("FromFragment" to 2, "Message" to it.message)
                    alertUserDialogFragment.arguments = bundle
                    alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                    dismiss()
                }
            })
    }

    private fun loadProfileActivity() {
        findNavController().navigate(R.id.userProfilePFragment)
        dismiss()
    }

}