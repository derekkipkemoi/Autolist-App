package org.carlistingapp.autolist.data.db.entities
import android.os.Parcel
import android.os.Parcelable
import java.util.HashSet


data class CarObject(
    var name: String? = null,
    val make: String? = null,
    val model: String? =null,
    val year: Float? = null,
    val body: String? = null,
    val condition: String? = null,
    val transmission: String? = null,
    val duty: String? = null,
    var mileage: Float? = null,
    val price: Float? = null,
    val priceNegotiable: Boolean? = true,
    val fuel: String? = null,
    val interior: String? = null,
    val color: String? = null,
    val engineSize: Float? =null,
    val description: String? = null,
    val features: HashSet<String>? = null,
    val location: String? = null
) : Parcelable {
    val id: String? = null
    val seller: Seller? = null
    val featured: Featured? = null
    val status: String? = null
    val images: List<String>? = null
    val views: Int? = null
    val createdAt: String? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        TODO("features"),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(make)
        parcel.writeString(model)
        parcel.writeValue(year)
        parcel.writeString(body)
        parcel.writeString(condition)
        parcel.writeString(transmission)
        parcel.writeString(duty)
        parcel.writeValue(mileage)
        parcel.writeValue(price)
        parcel.writeValue(priceNegotiable)
        parcel.writeString(fuel)
        parcel.writeString(interior)
        parcel.writeString(color)
        parcel.writeValue(engineSize)
        parcel.writeString(description)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CarObject> {
        override fun createFromParcel(parcel: Parcel): CarObject {
            return CarObject(parcel)
        }

        override fun newArray(size: Int): Array<CarObject?> {
            return arrayOfNulls(size)
        }
    }
}




