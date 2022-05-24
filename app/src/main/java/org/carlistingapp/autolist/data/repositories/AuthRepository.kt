package org.carlistingapp.autolist.data.repositories

import org.carlistingapp.autolist.data.db.entities.PasswordReset
import org.carlistingapp.autolist.data.db.entities.User
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.SafeAPIRequest
import org.carlistingapp.autolist.data.network.response.UserObjectResponse
import org.carlistingapp.autolist.data.network.response.Message
import org.carlistingapp.autolist.utils.ApiException

class AuthRepository(private val listingCarsAPI: ListingCarsAPI) : SafeAPIRequest() {
    //suspend fun getUsers() = apiRequest { listingCarsAPI.getUsers() }
    suspend fun userSignUp(user: User) : UserObjectResponse{
        return apiRequest { listingCarsAPI.userSignUp(user)}
    }

    suspend fun userLogin(user: User) : UserObjectResponse {
        return apiRequest {listingCarsAPI.getUserLogin(user)}
    }

    suspend fun userSignInWithGoogleAccount(token: UserObjectResponse) = apiRequest {listingCarsAPI.getUserGoogleSignIn(token)}

    suspend fun userSignInWithFacebookAccount(token: UserObjectResponse) = apiRequest {listingCarsAPI.getUserFacebookSignIn(token)}

    suspend fun requestPasswordResetLink(user: User) : Message {
        return apiRequest { listingCarsAPI.requestPasswordResetLink(user) }
    }

    suspend fun passwordReset(userId: String, passwordReset: PasswordReset, secretToken: String) : Message {
        return try {
            apiRequest { listingCarsAPI.passwordReset(userId, passwordReset, secretToken) }
        }catch (e:ApiException){
            Message(e.message!!)
        }
    }

}