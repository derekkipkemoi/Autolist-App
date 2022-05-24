package org.carlistingapp.autolist.data.network.response

import org.carlistingapp.autolist.data.db.entities.CarObject

data class CarObjectResponse(
    val carObject: CarObject? = null,
    val message: String? = null
)