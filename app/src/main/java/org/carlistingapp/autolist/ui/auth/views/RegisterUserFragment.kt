package org.carlistingapp.autolist.ui.auth.views

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.GoogleSignInAccessTokenDataClass
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.NetworkConnectionInterceptor
import org.carlistingapp.autolist.data.repositories.AuthRepository
import org.carlistingapp.autolist.databinding.FragmentRegisterUserBinding
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModel
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.utils.CustomAlertDialog
import org.carlistingapp.autolist.utils.Session
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterUserFragment : BottomSheetDialogFragment(), KodeinAware {
    override val kodein by kodein()
    private val networkConnectionInterceptor : NetworkConnectionInterceptor by instance()
    val api : ListingCarsAPI by instance()
    val repository : AuthRepository by instance()
    val factory: AuthViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel : AuthViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var binding: FragmentRegisterUserBinding
    private lateinit var callbackManager : CallbackManager
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val onBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // Handle the back button event
//                val startDestination = findNavController().graph.startDestination
//                val navOptions = NavOptions.Builder()
//                    .setPopUpTo(startDestination, true)
//                    .build()
//                findNavController().navigate(startDestination, null, navOptions)
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


        viewModel = ViewModelProvider(this.requireActivity(),factory).get(AuthViewModel ::class.java)
        //Google sign In option
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestServerAuthCode(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //Facebook CallbackManager
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                binding.progressBar.visibility = View.VISIBLE
                if (loginResult != null){
                    val accessToken = loginResult.accessToken?.token
                    if (accessToken != null) {
                        val customAlertDialog = CustomAlertDialog(requireActivity())
                        customAlertDialog.startLoadingDialog("Updating User Profile")
                            viewModel.getSignedInWithFacebookAccount(accessToken, requireContext())
                            viewModel.loggedInUseWithFacebookAccountResponse.observe(viewLifecycleOwner, Observer {
                                customAlertDialog.stopDialog()
                                if (it.access_token != null){
                                    binding.root.snackBar(it.message)
                                    session.put(it.userObject!!,"UserObject")
                                    session.saveUserName(it.userObject.facebook?.name.toString())
                                    session.saveUserEmail(it.userObject.facebook?.email.toString())
                                    session.saveSession(it.userObject.id)
                                    session.saveUserPicture(it.userObject.facebook?.picture.toString())
                                    if (it.userObject.phoneNumber?.verified == false){
                                        findNavController().navigate(R.id.userProfilePFragment)
                                        dismiss()
                                    }
                                    else{
                                        dismiss()
                                    }
                                }
                            })
                    }
                }
            }
            override fun onCancel() {
            }
            override fun onError(exception: FacebookException) {
                // App code
                binding.googleSignIn.isEnabled = true
                binding.facebookLogin.isEnabled = true
                binding.registerButton.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
               // binding.root.snackBar("Login with facebook failed!!")

                val alertUserDialogFragment = AlertUserDialogFragment()
                val bundle = bundleOf("FromFragment" to 2, "Message" to "Register with facebook failed!!")
                alertUserDialogFragment.arguments = bundle
                alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                dismiss()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_user, container, false)
        binding.googleSignIn.setOnClickListener {
            googleSignIn()
        }
        binding.facebookLoginHide.fragment = this
        binding.facebookLogin.setOnClickListener{
            binding.facebookLoginHide.performClick()
            binding.progressBar.visibility = View.VISIBLE
            binding.googleSignIn.isEnabled = false
            binding.facebookLogin.isEnabled = false
            binding.registerButton.isEnabled = false
        }
        binding.registerButton.setOnClickListener {
            signInUser()
        }

        binding.login.setOnClickListener {
            val logInUserFragment = LogInUserFragment()
            logInUserFragment.show(requireActivity().supportFragmentManager, "logInUserFragment")
            dismiss()
        }
        return binding.root
    }

    private fun googleSignIn() {
        binding.progressBar.visibility = View.VISIBLE
        binding.googleSignIn.isEnabled = false
        binding.facebookLogin.isEnabled = false
        binding.registerButton.isEnabled = false
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null){
                val authorizationCode = account.serverAuthCode.toString()
                val userTokenId = account.idToken.toString()
                binding.progressBar.visibility = View.INVISIBLE
                updateUI(authorizationCode,userTokenId)
            }
        } catch (e: ApiException) {
            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.statusCode)
            binding.googleSignIn.isEnabled = true
            binding.facebookLogin.isEnabled = true
            binding.registerButton.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
            binding.root.snackBar("Register with google failed!!")
        }
    }

    private fun updateUI(authorizationCode :String, userTokenId :String ){
        val carApi = ListingCarsAPI(networkConnectionInterceptor)
        binding.progressBar.visibility = View.VISIBLE
        val call =  carApi.getAccessTokenGoogle(
            url = getString(R.string.google_get_access_token_url),
            grant_type = "authorization_code",
            client_id = getString(R.string.web_client_id),
            client_secret = getString(R.string.web_client_secret),
            redirect_uri = "",
            authCode = authorizationCode,
            id_token = userTokenId)
        call.enqueue(object : Callback<GoogleSignInAccessTokenDataClass> {
            val tag = "getGoogleAccessToken"
            override fun onFailure(call: Call<GoogleSignInAccessTokenDataClass>, t: Throwable) {
                Log.e(tag, t.toString())
            }

            override fun onResponse(
                call: Call<GoogleSignInAccessTokenDataClass>,
                response: Response<GoogleSignInAccessTokenDataClass>
            ) {

                if (response.isSuccessful){
                    val responseBody = response.body()
                    val accessToken = responseBody!!.access_token
                    val customAlertDialog = CustomAlertDialog(requireActivity())
                    customAlertDialog.startLoadingDialog("Updating User Profile")
                    viewModel.getSignedInWithGoogleAccount(accessToken, requireContext())

                    viewModel.loggedInUseWithGoogleAccountResponse.observe(viewLifecycleOwner, Observer {
                        binding.progressBar.visibility = View.INVISIBLE
                        requireActivity().viewModelStore.clear()
                        customAlertDialog.stopDialog()
                        if (it.userObject?.id != null){
                            binding.root.snackBar(it.message)
                            session.put(it.userObject,"UserObject")
                            session.saveUserName(it.userObject.google?.name.toString())
                            session.saveUserEmail(it.userObject.google?.email.toString())
                            session.saveSession(it.userObject.id)
                            session.saveUserPicture(it.userObject.google?.picture.toString())
                            if (it.userObject.phoneNumber?.verified == false){
                                findNavController().navigate(R.id.userProfilePFragment)
                                dismiss()
                            }
                            else{
                                dismiss()
                            }
                        }

                    })

                }else{
                    try {
                        val responseError = response.errorBody()!!.string()
                        val alertUserDialogFragment = AlertUserDialogFragment()
                        val bundle = bundleOf("FromFragment" to 2, "Message" to responseError)
                        alertUserDialogFragment.arguments = bundle
                        alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                        dismiss()
                        //Log.e(tag, responseError)
                    }catch (e:Exception){
                        Log.e(tag, e.toString())}
                }
            }
        })
    }

    private fun signInUser() {
        val registerUserEmailPhoneFragment = RegisterUserPhoneEmailFragment()
        registerUserEmailPhoneFragment.show(requireActivity().supportFragmentManager, "registerUserEmailPhoneFragment")
        dismiss()
    }


    companion object {
        private const val RC_SIGN_IN = 100
    }

}

