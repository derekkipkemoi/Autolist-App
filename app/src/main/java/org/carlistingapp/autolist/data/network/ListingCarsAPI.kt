package org.carlistingapp.autolist.data.network
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import org.carlistingapp.autolist.data.db.entities.*
import org.carlistingapp.autolist.data.network.response.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
interface ListingCarsAPI {

    @Headers("Content-Type: application/json")
    @POST("users/registerUser")
    suspend fun userSignUp(@Body user: User) : Response<UserObjectResponse>

    @Headers("Content-Type: application/json")
    @POST("users/loginUser")
    suspend fun getUserLogin(@Body user: User) : Response<UserObjectResponse>

    @Headers("Content-Type: application/json")
    @POST("users/oauth/google")
    suspend fun getUserGoogleSignIn(@Body token: UserObjectResponse) : Response<UserObjectResponse>

    @FormUrlEncoded
    @POST
    fun getAccessTokenGoogle(
        @Url url: String,
        @Field("grant_type") grant_type: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_uri") redirect_uri: String,
        @Field("code") authCode: String,
        @Field("id_token") id_token: String
    ): Call<GoogleSignInAccessTokenDataClass>

//    @Headers("Content-Type: application/json")
//    @POST
//    suspend fun paymentSTKPush(
//        @Header("Authorization") authorization : String,
//        @Url url: String,
//        @Body stkPush: PaymentSTKPush
//    ) : Response<PaymentSTKPushResponse>

    @Headers("Content-Type: application/json")
    @POST("users/oauth/facebook")
    suspend fun getUserFacebookSignIn(@Body token: UserObjectResponse) : Response<UserObjectResponse>

    @GET("users/{userId}/cars")
    suspend fun getUserCars(@Path("userId") userId:String?) : Response<CarObjectsListResponse>

    @GET("users/{userId}/viewUserCars")
    suspend fun viewUserCars(@Path("userId") userId:String?) : Response<CarObjectsListResponse>

    @GET("payments/{userId}/carPayments")
    suspend fun getUserPayments(@Path("userId") userId:String?) : Response<PaymentResponse>

    @Headers("Content-Type: application/json")
    @POST("users/{userId}/phoneNumber")
    suspend fun verifyUserPhone(@Body phoneNumber: PhoneNumber, @Path("userId") userId:String?) : Response<UserObjectResponse>

    @Headers("Content-Type: application/json")
    @POST("users/{id}/cars")
    suspend fun postCar(@Body car: CarObject, @Path("id") id:String?) : Response<CarObjectResponse>

    @GET("cars/{carId}")
    suspend fun getCar(@Path("carId") carId:String?) : Response<CarObjectResponse>

    @GET("cars/listCars")
    suspend fun getCars() : Response<CarObjectsListResponse>

    @Headers("Content-Type: application/json")
    @PATCH("cars/{carId}")
    suspend fun updateUserCar(@Body carObject: CarObject, @Path("carId") carId:String?) : Response<CarObjectResponse>

    @Headers("Content-Type: application/json")
    @POST("cars/{carId}/carViewed")
    suspend fun carViewed(@Path("carId") carId:String?) : Response<Message>

    @Headers("Content-Type: application/json")
    @PATCH("users/{userId}")
    suspend fun updateUserName(@Body name: NameUpdate, @Path("userId") userId:String?) : Response<Message>

    @Headers("Content-Type: application/json")
    @GET("users/{userId}")
    suspend fun getUser(@Body name: NameUpdate, @Path("userId") userId:String?) : Response<UserObject>

    @Headers("Content-Type: application/json")
    @DELETE("cars/{carId}/deleteCar")
    suspend fun deleteUserCar(@Path("carId") carId:String?) : Response<Message>

    @Headers("Content-Type: application/json")
    @PATCH("cars/{carId}/carSold")
    suspend fun soldUserCar(@Path("carId") carId:String?) : Response<Message>

    @Headers("Content-Type: application/json")
    @PATCH("cars/{carId}/featureCar")
    suspend fun featureUserCar(@Path("carId") carId:String?, @Body featureUserCar: PaymentPackage) : Response<Message>

    @Headers("Content-Type: application/json")
    @POST("payments/{userId}/carPayments")
    suspend fun carPayment(@Path("userId") userId:String?, @Body payment: Payment) : Response<Message>

    @Multipart
    @POST("cars/{id}/carImages")
    suspend fun postCarImages(@Part photos: List<MultipartBody.Part>, @Path("id") id:String?) : Response<CarObjectResponse>

    @Multipart
    @POST("users/{id}/updateUserImage")
    suspend fun updateProfileImage(@Part photos: MultipartBody.Part, @Path("id") id:String?) : Response<ImageUrl>

    @Headers("Content-Type: application/json")
    @PATCH("cars/{id}/carImages")
    suspend fun deleteCarImage(@Body imageUrl: ImageUrl, @Path("id") id:String?) : Response<Message>

    @Headers("Content_Type: application/json")
    @POST("users/requestPasswordResetLink")
    suspend fun requestPasswordResetLink(@Body user: User) : Response<Message>

    @Headers("Content_Type: application/json")
    @POST("users/{userId}/resetPassword")
    suspend fun passwordReset(@Path("userId") userId: String?, @Body passwordReset: PasswordReset, @Header("Authorization") secretToken:String) : Response<Message>

    @Headers("Content_Type: application/json")
    @POST("users/{userId}/contactUs")
    suspend fun contactUs(@Path("userId") userId: String?, @Body contactUs: ContactUs) : Response<Message>

    @Headers("Content_Type: application/json")
    @POST("users/{userId}/{carId}/favouriteCarsList")
    suspend fun favouriteCar(@Path("userId") userId: String?, @Path("carId") carId: String?) : Response<UserObjectResponse>

    @Headers("Content_Type: application/json")
    @GET("users/{userId}/1/favouriteCarsList")
    suspend fun getUserFavouriteCars(@Path("userId") userId: String?) : Response<CarObjectsListResponse>

    @Headers("Content_Type: application/json")
    @POST("users/{userId}/userFcmToken")
    suspend fun userFcmToken(@Path("userId") userId: String?, @Body secretToken: Token) : Response<Message>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor) : ListingCarsAPI{
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES) // write timeout
                .readTimeout(2, TimeUnit.MINUTES) // read timeout
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://www.autolist.co.ke/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ListingCarsAPI::class.java)
        }
    }
}