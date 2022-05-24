package org.carlistingapp.autolist.data.db.entities

import androidx.room.Entity

@Entity
data class User(
    val email: String? = null,
    val password: String? = null,
    val name: String? = null
){
    val picture: String? = null
    val active: String? = null
}