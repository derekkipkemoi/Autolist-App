package org.carlistingapp.autolist.ui.home.profile.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import okhttp3.MultipartBody
import org.carlistingapp.autolist.Coroutines
import org.carlistingapp.autolist.data.db.entities.*
import org.carlistingapp.autolist.data.network.response.*
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.utils.Session

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userCars = MutableLiveData<CarObjectsListResponse>()
    val userCars : LiveData<CarObjectsListResponse> get() = _userCars


    private val _userPayments = MutableLiveData<PaymentResponse>()
    val userPayments : LiveData<PaymentResponse> get() = _userPayments

    private val _userCarResponse = MutableLiveData<CarObjectResponse>()
    val userCarResponse : LiveData<CarObjectResponse> get() = _userCarResponse

    private val _updateUserCarResponse = MutableLiveData<CarObjectResponse>()
    val updateUserCarResponse : LiveData<CarObjectResponse> get() = _updateUserCarResponse

    private val _postCarImagesResponse = MutableLiveData<CarObjectResponse>()
    val postCarImagesResponse : LiveData<CarObjectResponse> get() = _postCarImagesResponse

    private val _deleteCarImageResponse = MutableLiveData<Message>()
    val deleteCarImageResponse : LiveData<Message> get() = _deleteCarImageResponse

    private val _updateImageResponse = MutableLiveData<ImageUrl>()
    val updateImageResponse : LiveData<ImageUrl> get() = _updateImageResponse

    private val _deleteUserCar = MutableLiveData<Message>()
    val deleteUserCar : LiveData<Message> get() = _deleteUserCar

    private val _soldUserCar = MutableLiveData<Message>()
    val soldUserCar : LiveData<Message> get() = _soldUserCar

    private val _featureCar = MutableLiveData<Message>()
    val featureCar : LiveData<Message> get() = _featureCar

    private val _carPayment = MutableLiveData<Message>()
    val carPayment : LiveData<Message> get() = _carPayment

    private val _updateName = MutableLiveData<Message>()
    val updateName : LiveData<Message> get() = _updateName

    private val _userPhoneObject = MutableLiveData<UserObjectResponse>()
    val userPhoneObject : LiveData<UserObjectResponse> = _userPhoneObject

    private val _responseGeneralMessage = MutableLiveData<Message>()
    val responseGeneralMessage : LiveData<Message> get() = _responseGeneralMessage

    private lateinit var session : Session

    private lateinit var job: Job

    fun getUserCars(userID : String,  context: Context){
        job = Coroutines.ioThenMain({userRepository.getUserCars(userID)}, {_userCars.value = it}, context)
    }

    fun viewUserCars(userID : String, context: Context){
        job = Coroutines.ioThenMain({userRepository.viewUserCars(userID)}, {_userCars.value = it}, context)
    }

    fun getUserPayments(userID : String,  context: Context){
        job = Coroutines.ioThenMain({userRepository.getUserPayments(userID)}, {_userPayments.value = it}, context)
    }


    fun getUserCar(carId: String, context: Context){
        job = Coroutines.ioThenMain({userRepository.getCar(carId)}, {_userCarResponse.value = it}, context)
    }

    fun getUserFavouriteCars(userID : String, context: Context){
        job = Coroutines.ioThenMain({userRepository.getUserFavouriteCars(userID)}, {_userCars.value = it}, context)
    }

    fun updateUserCar(carObject : CarObject, carId: String,context: Context)
    {
        job = Coroutines.ioThenMain({userRepository.updateUserCar(carObject,carId )},{_updateUserCarResponse.value = it}, context)
    }

    fun deleteCarImage(objectString: ImageUrl, carId: String?, context: Context)
    {
        job = Coroutines.ioThenMain({userRepository.deleteCarImage(objectString,carId)},{_deleteCarImageResponse.value = it}, context)
    }

    fun updateProfileImage(userImageObject: MultipartBody.Part, userId: String?, context: Context)
    {
        job = Coroutines.ioThenMain({userRepository.updateProfileImage(userImageObject,userId)},{_updateImageResponse.value = it}, context)
    }

    fun postCarImages(carObjectImages: List<MultipartBody.Part>, carId : String?, context: Context)
    {
        job = Coroutines.ioThenMain({userRepository.postUserCarImages(carObjectImages,carId)},{_postCarImagesResponse.value = it}, context)
    }


    fun updateUserName(name: NameUpdate, userId : String, context: Context){
        job = Coroutines.ioThenMain({userRepository.updateUserName(name, userId)}, {_updateName.value = it}, context )
    }

    fun verifyUserPhoneNumber(context: Context, userId: String,phoneNumber: PhoneNumber){
            job = Coroutines.ioThenMain({userRepository.verifyUserPhoneNumber(phoneNumber,userId )},{_userPhoneObject.value = it}, context)
    }

    fun deleteUserCar(carId : String,context: Context){
        job = Coroutines.ioThenMain({userRepository.deleteUserCar(carId)}, {_deleteUserCar.value = it}, context )
    }

    fun soldUserCar(carId : String, view: View, context: Context){
        job = Coroutines.ioThenMain({userRepository.soldUserCar(carId)}, {_soldUserCar.value = it}, context )
    }

    fun featureCar(carId: String, featureUserCar: PaymentPackage, view: View, context: Context){
        job = Coroutines.ioThenMain({userRepository.featureUserCar(carId, featureUserCar)}, {_featureCar.value = it},context )
    }

    fun carPayment(userId: String, payment: Payment, context: Context){
        job = Coroutines.ioThenMain({userRepository.carPayment(userId, payment)}, {_carPayment.value = it}, context )
    }

    fun contactUs(userId: String, contactUs: ContactUs,  context: Context){
        job = Coroutines.ioThenMain({userRepository.contactUs(userId, contactUs)}, {_responseGeneralMessage.value = it}, context )
    }

    fun userFcmToken(userId: String, token: Token,  context: Context){
        job = Coroutines.ioThenMain({userRepository.userFcmToken(userId, token)}, {_responseGeneralMessage.value = it}, context )
    }




    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}