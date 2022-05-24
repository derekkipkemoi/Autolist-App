package org.carlistingapp.autolist.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import org.carlistingapp.autolist.data.db.entities.CarObject


class Session(context: Context) {

    private val userSessionKey : String = "userID"
    private val userNameKey : String = "userName"
    private val userEmailKey : String = "userEmail"
    private val userPictureKey : String = "userPicture"
    private val privatePreferenceName : String = "privatePreferenceName"
    private val userFcmTokenKey : String = "userFcmToken"


    var array = JsonArray()


    val sharedPreferences : SharedPreferences = context.getSharedPreferences(
        privatePreferenceName,
        Context.MODE_PRIVATE
    )
    private val editor : SharedPreferences.Editor = sharedPreferences.edit()

    fun saveSession(userID: String?){
        editor.putString(userSessionKey, userID).commit()
    }
    fun getSession() : String? {
        return sharedPreferences.getString(userSessionKey, "userLoggedOut")
    }

    fun saveUserName(userName: String){
       editor.putString(userNameKey, userName).commit()
    }

    fun getUserName() : String?{
        return sharedPreferences.getString(userNameKey, "noUserName")
    }

    fun saveUserEmail(userEmail: String){
        editor.putString(userEmailKey, userEmail).commit()
    }

    fun  getUserEmail() : String?{
        return sharedPreferences.getString(userEmailKey, "noUserEmail")
    }

    fun saveUserPicture(userPicture: String){
        editor.putString(userPictureKey, userPicture).commit()
    }

    fun  getUserFcmToken() : String?{
        return sharedPreferences.getString(userFcmTokenKey, "noUserFcmToken")
    }
    fun saveUserFcmToken(userFcmToken: String){
        editor.putString(userFcmTokenKey, userFcmToken).commit()
    }

    fun  getUserPicture() : String?{
        return sharedPreferences.getString(userPictureKey, "noUserPicture")
    }



    fun clearUserFavouriteCars(){
        sharedPreferences.edit().remove("favouriteCars").apply()
    }


    fun saveUserFavoriteCars(list: List<String>){
        val gson = Gson()
        val json = gson.toJson(list)//converting list to Json
        editor.putString("favouriteCars", json)
        editor.commit()
    }
    //getting the list from shared preference

    fun getUserUserFavoriteCars(): List<String> {
        val emptyList = Gson().toJson(ArrayList<String>())
        return Gson().fromJson(
            sharedPreferences.getString("favouriteCars", emptyList),
            object : TypeToken<List<String>>() {}.type
        )
    }


    fun saveRecentViewedCars(carObject: CarObject){
        val emptyList = Gson().toJson(ArrayList<String>())
        val savedCarsList : ArrayList<String> = Gson().fromJson(
            sharedPreferences.getString("viewedCars", emptyList),
            object : TypeToken<ArrayList<String>>() {}.type
        )
            if (savedCarsList.contains(carObject.id)){
                return
            }
            else{
                savedCarsList.add(carObject.id!!)
            }
        val gson = Gson()
        val json = gson.toJson(savedCarsList)//converting list to Json
        editor.putString("viewedCars", json)
        editor.commit()
    }
    //getting the list from shared preference

    fun getRecentViewedCars(): ArrayList<String> {
        val emptyList = Gson().toJson(ArrayList<String>())
        return Gson().fromJson(
            sharedPreferences.getString("viewedCars", emptyList),
            object : TypeToken<ArrayList<String>>() {}.type
        )
    }

    fun clearRecentViewedCars(){
        sharedPreferences.edit().remove("viewedCars").apply()
    }


    fun saveRecentSearchedText(textSearched: String){
        val emptyList = Gson().toJson(ArrayList<String>())
        val savedTextList : ArrayList<String> = Gson().fromJson(
            sharedPreferences.getString("textSearched", emptyList),
            object : TypeToken<ArrayList<String>>() {}.type
        )

        if (savedTextList.contains(textSearched)){
            return
        }
        else{
            savedTextList.add(textSearched)
        }

        val gson = Gson()
        val json = gson.toJson(savedTextList)//converting list to Json
        editor.putString("textSearched", json)
        editor.commit()
    }
    //getting the list from shared preference

    fun getRecentSearchedText(): ArrayList<String> {
        val emptyList = Gson().toJson(ArrayList<String>())
        return Gson().fromJson(
            sharedPreferences.getString("textSearched", emptyList),
            object : TypeToken<ArrayList<String>>() {}.type
        )
    }

    fun clearRecentSearchedText(){
        sharedPreferences.edit().remove("textSearched").apply()
    }


    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        sharedPreferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = sharedPreferences.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }
}