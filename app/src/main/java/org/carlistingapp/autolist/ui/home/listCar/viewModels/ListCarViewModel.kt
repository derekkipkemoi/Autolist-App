package org.carlistingapp.autolist.ui.home.listCar.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import org.carlistingapp.autolist.Coroutines
import org.carlistingapp.autolist.data.network.response.CarObjectsListResponse
import org.carlistingapp.autolist.data.network.response.Message
import org.carlistingapp.autolist.data.network.response.UserObjectResponse
import org.carlistingapp.autolist.data.repositories.UserRepository

class ListCarViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _cars = MutableLiveData<CarObjectsListResponse>()
    val cars : LiveData<CarObjectsListResponse> get() = _cars

    private var _carViewed = MutableLiveData<Message>()
    val carViewed : LiveData<Message> get() = _carViewed
    private lateinit var job: Job

    private val _addCarToFavouriteList = MutableLiveData<UserObjectResponse>()
    val addCarToFavouriteList : LiveData<UserObjectResponse> get() = _addCarToFavouriteList

    fun getCars(context: Context){
        job = Coroutines.ioThenMain(
            {userRepository.getCars()}, {
                _cars.value = it}, context)
    }

    fun carViewed(carId : String,context: Context){
        job = Coroutines.ioThenMain(
            {userRepository.carViewed(carId)}, {
                _carViewed.value = it},context)
    }

    fun favouriteCar(userId: String, carId: String, context: Context){
        job = Coroutines.ioThenMain({userRepository.favouriteCar(userId, carId)}, {
            _addCarToFavouriteList.value = it}, context )
    }


    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}