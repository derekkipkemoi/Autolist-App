package org.carlistingapp.autolist.ui.home.profile.views
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.PhoneNumber
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentVerifyPhoneCodeBinding
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.CustomAlertDialog
import org.carlistingapp.autolist.utils.Session
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class VerifyPhoneCodeFragment : BottomSheetDialogFragment() , KodeinAware {
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory: UserViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentVerifyPhoneCodeBinding
    private lateinit var verificationID: String
    private lateinit var phoneNumber : String
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_verify_phone_code, container, false)
        val view = binding.root
        verificationID = arguments?.getString("verificationID").toString()
        phoneNumber = arguments?.getString("phoneNumber").toString()
        Log.i("Phone Number", phoneNumber)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this.requireActivity(),factory).get(UserViewModel ::class.java)
        binding.buttonVerifyCode.setOnClickListener {
            verifySentCode()
        }


        return view
    }

    @SuppressLint("SetTextI18n")
    private fun verifySentCode(){
        val optView = binding.otpView
        val code = optView.text.toString().trim()

        if (code.isEmpty()){
            binding.warningText.visibility = View.VISIBLE
            binding.warningText.text = "Enter Verification Code Sent To Provided Number"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.warningText.visibility = View.INVISIBLE
        val credential = PhoneAuthProvider.getCredential(verificationID, code)

        signInWithPhoneAuthCredential(credential)
    }

    @SuppressLint("SetTextI18n")
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.buttonVerifyCode.isEnabled = false
                    binding.successText.visibility = View.VISIBLE
                    binding.successText.text = "Code Verified Successfully"
                    val customAlertDialog = CustomAlertDialog(requireActivity())
                    customAlertDialog.startLoadingDialog("Updating User Phone Number")
                    val userId  = session.getSession()
                    val phoneNumber = PhoneNumber(phoneNumber)
                    viewModel.verifyUserPhoneNumber(requireContext(),userId!!,phoneNumber)
                    binding.progressBar.visibility = View.INVISIBLE
                    viewModel.userPhoneObject.observe(viewLifecycleOwner, Observer { userPhoneUpdate ->
                        customAlertDialog.stopDialog()
                        binding.root.snackBar(userPhoneUpdate.message)
                        if (userPhoneUpdate.userObject?.phoneNumber?.verified == true){
                            Toast.makeText(requireContext(),"User Phone Number Updated Successfully",Toast.LENGTH_LONG).show()
                            session.put(userPhoneUpdate.userObject, "UserObject")
                            findNavController().popBackStack()
                            dismiss()
                        }
                    })

                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.buttonVerifyCode.isEnabled = true
                        binding.warningText.visibility = View.VISIBLE
                        binding.warningText.text = "The verification code entered was invalid"
                    }
                }
            }
    }
}