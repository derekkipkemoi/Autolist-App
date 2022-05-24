package org.carlistingapp.autolist.ui.auth.views

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
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
import org.carlistingapp.autolist.databinding.FragmentLogInUserBinding
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModel
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.utils.CustomAlertDialog
import org.carlistingapp.autolist.utils.NoInternetException
import org.carlistingapp.autolist.utils.Session
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class LogInUserFragment : BottomSheetDialogFragment(), KodeinAware {
    override val kodein by kodein()
    private val networkConnectionInterceptor : NetworkConnectionInterceptor by instance()
    val api : ListingCarsAPI by instance()
    val repository : AuthRepository by instance()
    val factory: AuthViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel : AuthViewModel
    private lateinit var savedStateHandle: SavedStateHandle


    private lateinit var binding: FragmentLogInUserBinding
    private lateinit var callbackManager : CallbackManager
    private lateinit var mGoogleSignInClient : GoogleSignInClient

    private val passwordPattern = Pattern.compile("^" +
            //"(?=.*[0-9])" +  /
            // /at least 1 digit
            // "(?=.*[a-z])" +       //at least 1 lower case letter
            //"(?=.*[A-Z])" +       //at least 1 upper case letter
            //"(?=.*[a-zA-Z])" +    //any letter
            //"(?=.*[@#$%^&+=])" +  //at least 1 special character
            //"(?=\\S+$)" +         //no white spaces
            ".{4,}" +               //at least 4 characters
            "$"
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                val startDestination = findNavController().graph.startDestination
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(startDestination, true)
                    .build()
                findNavController().navigate(startDestination, null, navOptions)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
        LoginManager.getInstance().loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    binding.progressBar.visibility = View.VISIBLE
                    if (loginResult != null){
                        val accessToken = loginResult.accessToken?.token
                        if (accessToken != null) {
                            val customAlertDialog = CustomAlertDialog(requireActivity())
                            customAlertDialog.startLoadingDialog("Updating User Profile")
                            viewModel.getSignedInWithFacebookAccount(accessToken, requireContext())
                                viewModel.loggedInUseWithFacebookAccountResponse.observe(viewLifecycleOwner, Observer {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    customAlertDialog.stopDialog()
                                    requireActivity().viewModelStore.clear()
                                    if (it.access_token != null){
                                        binding.root.snackBar(it.message)
                                        session.put(it.userObject!!, "UserObject")
                                        session.saveUserName(it.userObject.facebook?.name.toString())
                                        session.saveUserEmail(it.userObject.facebook?.email.toString())
                                        session.saveSession(it.userObject.id)
                                        session.saveUserPicture(it.userObject.facebook?.picture.toString())
                                        if (!it.userObject.favouriteCars.isNullOrEmpty()){
                                            session.saveUserFavoriteCars(it.userObject.favouriteCars)
                                            dismiss()
                                        }

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
                    binding.googleSignIn.isEnabled = true
                    binding.facebookLogin.isEnabled = true
                    binding.loginButton.isEnabled = true
                    binding.progressBar.visibility = View.INVISIBLE
                    val alertUserDialogFragment = AlertUserDialogFragment()
                    val bundle = bundleOf("FromFragment" to 1, "Message" to "Login with facebook failed!!")
                    alertUserDialogFragment.arguments = bundle
                    alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                    dismiss()
                }
            })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_log_in_user, container, false)
        val view = binding.root

        dialog?.setCanceledOnTouchOutside(true);

        binding.googleSignIn.setOnClickListener {
            googleSignIn()
        }
        binding.facebookLoginHide.fragment = this
        binding.facebookLogin.setOnClickListener{
            binding.facebookLoginHide.performClick()
            binding.progressBar.visibility = View.VISIBLE
            binding.googleSignIn.isEnabled = false
            binding.facebookLogin.isEnabled = false
            binding.loginButton.isEnabled = false
        }

        binding.loginButton.setOnClickListener {
            signInUser()

        }

        binding.register.setOnClickListener {
            val registerUserFragment = RegisterUserFragment()
            registerUserFragment.show(requireActivity().supportFragmentManager, "RegisterUserFragment")
            dismiss()
        }

        binding.forgotPassword.setOnClickListener {
            val forgotPasswordFragment = ForgotPasswordFragment()
            forgotPasswordFragment.show(requireActivity().supportFragmentManager, "ForgotPasswordFragment")
            dismiss()
        }
        return view
    }

    private fun googleSignIn() {
        binding.progressBar.visibility = View.VISIBLE
        binding.googleSignIn.isEnabled = false
        binding.facebookLogin.isEnabled = false
        binding.loginButton.isEnabled = false
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LogInUserFragment.RC_SIGN_IN) {
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
            binding.loginButton.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
            binding.root.snackBar("Login with google failed!!")
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
         call.enqueue(object : Callback<GoogleSignInAccessTokenDataClass>{
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
                    // Toast.makeText(requireContext(),accessToken,Toast.LENGTH_LONG).show()
                     viewModel.getSignedInWithGoogleAccount(accessToken, requireContext())

                         viewModel.loggedInUseWithGoogleAccountResponse.observe(viewLifecycleOwner, Observer {
                             binding.progressBar.visibility = View.INVISIBLE
                             requireActivity().viewModelStore.clear()
                             customAlertDialog.stopDialog()
                             if (it.access_token != null){
                                 binding.root.snackBar(it.message)
                                 session.put(it.userObject!!,"UserObject")
                                 session.saveUserName(it.userObject.google?.name.toString())
                                 session.saveUserEmail(it.userObject.google?.email.toString())
                                 session.saveSession(it.userObject.id)
                                 session.saveUserPicture(it.userObject.google?.picture.toString())
                                 if (!it.userObject.favouriteCars.isNullOrEmpty()){
                                     session.saveUserFavoriteCars(it.userObject.favouriteCars)
                                     dismiss()
                                 }

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
                                 val bundle = bundleOf("FromFragment" to 1, "Message" to responseError)
                                 alertUserDialogFragment.arguments = bundle
                                 alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                                 dismiss()
                                 //Log.e(tag, responseError)
                             }catch (e:Exception){Log.e(tag, e.toString())}
                         }
             }
         })
     }

    private fun signInUser() {
        val email = binding.textViewEmail.text.toString().trim()
        val password = binding.textViewPassword.text.toString().trim()

        binding.emailTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailTextInputLayout.error = "Valid Email Address Required!!"
            return
        }
        if (!passwordPattern.matcher(password).matches()){
            binding.passwordTextInputLayout.error = "Password Should Have At Least 4 Characters!!"
            return
        }

        val customAlertDialog = CustomAlertDialog(requireActivity())
        customAlertDialog.startLoadingDialog("Logging In User")
        binding.progressBar.visibility = View.VISIBLE
        try {
            viewModel.logInUser(email,password, requireContext())
            viewModel.loggedInUserResponse.observe(viewLifecycleOwner, Observer {
                customAlertDialog.stopDialog()
                binding.progressBar.visibility = View.INVISIBLE
                requireActivity().viewModelStore.clear()
                if (it.access_token != null){
                    binding.root.snackBar(it.message)
                    session.put(it.userObject!!,"UserObject")
                    session.saveUserName(it.userObject.local?.name.toString())
                    session.saveUserEmail(it.userObject.local?.email.toString())
                    session.saveSession(it.userObject.id)
                    session.saveUserPicture(it.userObject.local?.picture.toString())
                    if (!it.userObject.favouriteCars.isNullOrEmpty()){
                        session.saveUserFavoriteCars(it.userObject.favouriteCars)
                    }
                    if (it.userObject.phoneNumber?.verified == false){
                        findNavController().navigate(R.id.userProfilePFragment)
                        dismiss()
                    }
                    else{
                        dismiss()
                    }
                }
                else{
                   // binding.root.snackBar(it.message)

                    val alertUserDialogFragment = AlertUserDialogFragment()
                    val bundle = bundleOf("FromFragment" to 1, "Message" to it.message)
                    alertUserDialogFragment.arguments = bundle
                    alertUserDialogFragment.show(requireActivity().supportFragmentManager, "alertUserDialogFragment")
                    dismiss()
                }
            })
        }catch (e : NoInternetException){
            binding.root.snackBar(e.message)
        }
    }



    companion object {
        private const val RC_SIGN_IN = 100
    }

}