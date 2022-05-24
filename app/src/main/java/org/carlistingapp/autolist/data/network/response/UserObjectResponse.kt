package org.carlistingapp.autolist.data.network.response

import org.carlistingapp.autolist.data.db.entities.UserObject

data class UserObjectResponse(
    val access_token: String? = null
){
    val message: String? = null
    val userObject: UserObject? = null
}

