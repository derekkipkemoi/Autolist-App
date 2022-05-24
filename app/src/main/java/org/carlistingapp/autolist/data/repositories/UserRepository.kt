package org.carlistingapp.autolist.data.repositories

import okhttp3.MultipartBody
import org.carlistingapp.autolist.data.db.entities.*
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.SafeAPIRequest
import org.carlistingapp.autolist.data.network.response.UserObjectResponse
import org.carlistingapp.autolist.data.network.response.CarObjectResponse
import org.carlistingapp.autolist.data.network.response.Message

class UserRepository(private val listingCarsAPI: ListingCarsAPI) : SafeAPIRequest(){
    //Get All Cars
    suspend fun getCars() =
        apiRequest { listingCarsAPI.getCars() }

    suspend fun getCar(carId: String?) =
        apiRequest { listingCarsAPI.getCar(carId) }


    suspend fun carViewed(carId: String) = apiRequest { listingCarsAPI.carViewed(carId) }
    //Post User Cars
    suspend fun postUserCar(carObject: CarObject, userId : String?) : CarObjectResponse{
       return apiRequest { listingCarsAPI.postCar(carObject,userId) }
    }

    //Update User Car
    suspend fun updateUserCar(carUpdate: CarObject, carId: String?) : CarObjectResponse {
        return apiRequest { listingCarsAPI.updateUserCar(carUpdate, carId) }
    }

    //Get user Cars
    suspend fun getUserCars(userId : String?) = apiRequest { listingCarsAPI.getUserCars(userId) }

    //Get user favourite cars
    suspend fun getUserFavouriteCars(userId : String?) = apiRequest { listingCarsAPI.getUserFavouriteCars(userId) }

    //View user Cars
    suspend fun viewUserCars(userId : String?) = apiRequest { listingCarsAPI.viewUserCars(userId) }

    //Get user Payments
    suspend fun getUserPayments(userId : String?) = apiRequest { listingCarsAPI.getUserPayments(userId) }

    //Delete user Cars
    suspend fun deleteUserCar(carId: String?) : Message {
        return apiRequest { listingCarsAPI.deleteUserCar(carId) }
    }

    //Delete user Cars
    suspend fun soldUserCar(carId: String?) : Message {
        return apiRequest { listingCarsAPI.soldUserCar(carId) }
    }

    //Feature user Cars
    suspend fun featureUserCar(carId: String?, featureUserCar: PaymentPackage) : Message {
        return  apiRequest { listingCarsAPI.featureUserCar(carId, featureUserCar) }
    }

    //car payment
    suspend fun carPayment(userId: String?, payment: Payment) : Message {
        return  apiRequest { listingCarsAPI.carPayment(userId, payment) }
    }


    //Update User
    suspend fun updateUserName(name : NameUpdate, userId : String) = apiRequest {listingCarsAPI.updateUserName(name, userId)}

    //Verify User Phone
    suspend fun verifyUserPhoneNumber(phoneNumber : PhoneNumber , userId : String?) : UserObjectResponse {
        return apiRequest { listingCarsAPI.verifyUserPhone(phoneNumber,userId) }
    }

    //Post User Car Images
    suspend fun postUserCarImages(carObjectImages: List<MultipartBody.Part>, carId : String?) : CarObjectResponse {
       return apiRequest { listingCarsAPI.postCarImages(carObjectImages,carId) }
    }

    //Update User Car Images
    suspend fun deleteCarImage(imageUrl: ImageUrl, carId : String?) : Message {
        return apiRequest { listingCarsAPI.deleteCarImage(imageUrl,carId) }
    }

    //Update User Profile Image
    suspend fun updateProfileImage(userObjectImage: MultipartBody.Part, userId : String?) : ImageUrl {
        return apiRequest { listingCarsAPI.updateProfileImage(userObjectImage,userId) }
    }

    //car payment
    suspend fun contactUs(userId: String?, contactUs: ContactUs) : Message {
        return  apiRequest { listingCarsAPI.contactUs(userId, contactUs) }
    }

    //favourite car
    suspend fun favouriteCar(userId: String?, carId: String) : UserObjectResponse {
        return  apiRequest { listingCarsAPI.favouriteCar(userId, carId) }
    }

    //firebase messaging token
    suspend fun userFcmToken(userId: String?, token: Token) : Message {
        return  apiRequest { listingCarsAPI.userFcmToken(userId, token) }
    }
}