package org.carlistingapp.autolist.ui.home.profile.views

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hbb20.CountryCodePicker
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.databinding.FragmentPhoneNumberBinding
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class PhoneNumberFragment : BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var ccp : CountryCodePicker
    private lateinit var binding: FragmentPhoneNumberBinding
    private lateinit var callBack : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var number: String
    private val PHONE_PATTERN  = Pattern.compile(
        //"^\\\\s*(?:\\\\+?(\\\\d{1,3}))?[-. (]*(\\\\d{3})[-. )]*(\\\\d{3})[-. ]*(\\\\d{4})(?: *x(\\\\d+))?\\\\s*\$"
        "\\d{10}"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_number, container, false)
        val view = binding.root

        callBack = object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredential:  PhoneAuthCredential) {
            }

            override fun onVerificationFailed(onVerificationFailed: FirebaseException) {
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                binding.progressBar.visibility = View.INVISIBLE
                val bundle = bundleOf("verificationID" to verificationId,"phoneNumber" to binding.textViewPhoneNumber.text?.trim().toString())
                val verifyPhoneCodeFragment = VerifyPhoneCodeFragment()
                verifyPhoneCodeFragment.arguments = bundle
                verifyPhoneCodeFragment.show(requireActivity().supportFragmentManager,"verifyPhoneCodeFragment")
                dismiss()
            }
        }

        
        binding.buttonVerify.setOnClickListener {
            sendCode()
        }
        return view
    }

    private fun sendCode(){
        ccp = binding.ccp
        val countryCode = ccp.selectedCountryCodeWithPlus
        number = binding.textViewPhoneNumber.text?.trim().toString()



        binding.phoneTextInputLayout.error = null
        if (number.isEmpty()){
            binding.phoneTextInputLayout.error = "Phone Number Required"
            return
        }
        if (!PHONE_PATTERN.matcher(number).matches()){
            binding.phoneTextInputLayout.error = "Enter Correct Phone Number"
            return
        }

        val phoneNumber = countryCode.plus(number).trim()
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonVerify.isEnabled = false



        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callBack)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}