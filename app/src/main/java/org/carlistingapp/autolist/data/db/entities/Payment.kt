package org.carlistingapp.autolist.data.db.entities

data class Payment(
    val carName: String? = null,
    val paymentPackage: PaymentPackage? = null,
    val paymentMessage: String? = null
){
    val payer: String? = null
    val message: String? = null
}