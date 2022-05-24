package org.carlistingapp.autolist.data.db.entities

data class Seller(
    val sellerEmail: String,
    val sellerID: String,
    val sellerNumber: Int,
    val sellerAvailableSince: String,
    val sellerName: String,
    val sellerPhoto: String
)