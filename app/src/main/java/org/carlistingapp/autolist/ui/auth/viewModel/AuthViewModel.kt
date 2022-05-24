package org.carlistingapp.autolist.ui.auth.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import org.carlistingapp.autolist.Coroutines
import org.carlistingapp.autolist.data.db.entities.User
import org.carlistingapp.autolist.data.network.response.UserObjectResponse
import org.carlistingapp.autolist.data.db.entities.GoogleSignInAccessTokenDataClass
import org.carlistingapp.autolist.data.db.entities.PasswordReset
import org.carlistingapp.autolist.data.network.response.Message
import org.carlistingapp.autolist.data.repositories.AuthRepository

class AuthViewModel(private val repository: AuthRepository) : ViewModel(){

    private lateinit var job: Job
    private val _loggedInUserResponse = MutableLiveData<UserObjectResponse>()
    val loggedInUserResponse : LiveData<UserObjectResponse> get() = _loggedInUserResponse

    private val _loggedInUseWithGoogleAccountResponse = MutableLiveData<UserObjectResponse>()
    val loggedInUseWithGoogleAccountResponse : LiveData<UserObjectResponse> get() = _loggedInUseWithGoogleAccountResponse

    private val _googleAccessToken = MutableLiveData<GoogleSignInAccessTokenDataClass>()
    val googleAccessToken : LiveData<GoogleSignInAccessTokenDataClass> get() = _googleAccessToken

    private val _loggedInUseWithFacebookAccountResponse = MutableLiveData<UserObjectResponse>()
    val loggedInUseWithFacebookAccountResponse : LiveData<UserObjectResponse> get() = _loggedInUseWithFacebookAccountResponse


    private val _registeredUserResponse = MutableLiveData<UserObjectResponse>()
    val registeredUserResponse : LiveData<UserObjectResponse> get() = _registeredUserResponse

    private val _passwordReset = MutableLiveData<Message>()
    val passwordResetResponse : LiveData<Message> get() = _passwordReset

    fun getSignedUpUser(user: User,context: Context)  {
            job = Coroutines.ioThenMain({
                repository.userSignUp(user)},{
                _registeredUserResponse.value = it}, context)
    }

    fun getSignedInWithGoogleAccount(token: String, context: Context)  {
        val userToken = UserObjectResponse(token)
            job = Coroutines.ioThenMain({
                repository.userSignInWithGoogleAccount(userToken)},{
                _loggedInUseWithGoogleAccountResponse.value = it}, context)
    }

    fun getSignedInWithFacebookAccount(token: String, context: Context)  {
        val userToken = UserObjectResponse(token)
        job = Coroutines.ioThenMain({repository.userSignInWithFacebookAccount(userToken)},{_loggedInUseWithFacebookAccountResponse.value = it},context)
    }

    fun logInUser(email :String, password : String, context: Context) {
        val userObject = User(email,password)
        job = Coroutines.ioThenMain({
            repository.userLogin(userObject)}, { _loggedInUserResponse.value = it
        },context)
    }

    fun requestPasswordResetLink(email :String, context: Context) {
        val user = User(email)
        job = Coroutines.ioThenMain({
            repository.requestPasswordResetLink(user)}, { _passwordReset.value = it
        }, context)
    }

    fun passwordReset(userId: String, password :String, secretToken: String, context: Context) {
        val passwordReset = PasswordReset(password)
            job = Coroutines.ioThenMain({
                repository.passwordReset(userId, passwordReset,secretToken)
            }, { _passwordReset.value = it },context)
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}