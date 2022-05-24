package org.carlistingapp.autolist.data.network

import android.content.ContentValues.TAG
import android.util.Log
import org.carlistingapp.autolist.utils.ApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeAPIRequest {
    suspend fun <T: Any> apiRequest(call: suspend () -> Response<T>) : T{
        val response = call.invoke()
        if (response.isSuccessful){
            return response.body()!!
        }
        else{
            val error = response.errorBody().toString()
            Log.d(TAG, error)
            val errorMessage = StringBuilder()
            error.let {
                try {
                    errorMessage.append(JSONObject(it).getJSONObject("error"))
                }catch (e : JSONException){ }
                errorMessage.append("\n")
            }
            errorMessage.append("Error Code: ${response.code()}")
            throw ApiException(errorMessage.toString())
        }
    }
}

