package org.carlistingapp.autolist.ui.home.postCar.viewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import okhttp3.MultipartBody
import org.carlistingapp.autolist.Coroutines
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.response.CarObjectResponse
import org.carlistingapp.autolist.data.repositories.UserRepository

class PostCarViewModel(private val repository: UserRepository) : ViewModel() {

    private val _postCarResponse = MutableLiveData<CarObjectResponse>()
    val postCarResponse : LiveData<CarObjectResponse> get() = _postCarResponse

    private val _postCarImagesResponse = MutableLiveData<CarObjectResponse>()
    val postCarImagesResponse : LiveData<CarObjectResponse> get() = _postCarImagesResponse
    private lateinit var job: Job


    fun postUserCar(carObject : CarObject, userId : String, view : View, context: Context)
    {
        job = Coroutines.ioThenMain({repository.postUserCar(carObject,userId )},{_postCarResponse.value = it}, context)
    }


    fun postUserCarImages(carObjectImages: List<MultipartBody.Part>, carId : String?, view: View, context: Context)
    {
        job = Coroutines.ioThenMain({repository.postUserCarImages(carObjectImages,carId)},{_postCarImagesResponse.value = it}, context)
    }

    override fun onCleared()
    {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}