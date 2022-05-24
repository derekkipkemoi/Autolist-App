package org.carlistingapp.autolist.data.db.entities

data class UserObject(
    val id: String? = null,
    val cars: List<String>? = null,
    val favouriteCars : ArrayList<String>? = null,
    val method: String? = null,
    val local: User? = null,
    val facebook: User? = null,
    val google: User? = null,
    val phoneNumber: PhoneNumber? = null
){
}