package com.example.flushit.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Model class for a toilet.
 */
class Toilet() : Parcelable {

    var id: String? = null
    var name: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var description: String? = null
    var price: String? = "Free"
    var averageRating: Float = 0f
    var nrOfRaters: Int = 0
    var totalRating: Float = 0f
    var imageURL: String? = null

    companion object{
        @JvmField
        val CREATOR = object : Parcelable.Creator<Toilet>{
            override fun createFromParcel(parcel:Parcel) = Toilet(parcel)
            override fun newArray(size:Int) = arrayOfNulls<Toilet>(size)
        }
    }

    private constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        description = parcel.readString()
        price = parcel.readString()
        averageRating = parcel.readFloat()
        nrOfRaters = parcel.readInt()
        totalRating = parcel.readFloat()
        imageURL = parcel.readString()
    }

    constructor(
        _id: String,
        _name: String,
        _latitude: Double,
        _longitude: Double,
        _description: String,
        _price: String,
        _averageRating: Float,
        _nrOfRaters: Int,
        _totalRating: Float,
        _imageURL: String
    ) : this() {
        id = _id
        name = _name
        latitude = _latitude
        longitude = _longitude
        description = _description
        price = _price
        averageRating = _averageRating
        nrOfRaters = _nrOfRaters
        totalRating = _totalRating
        imageURL = _imageURL
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.let {
            it.writeString(id)
            it.writeString(name)
            it.writeDouble(latitude)
            it.writeDouble(longitude)
            it.writeString(description)
            it.writeString(price)
            it.writeFloat(averageRating)
            it.writeInt(nrOfRaters)
            it.writeFloat(totalRating)
            it.writeString(imageURL)
        }

    }

    override fun describeContents() = 0

    /**
     * If the toilets have the same name and position, they are equal.
     */
    override fun equals(other: Any?): Boolean{
        if(other?.javaClass != javaClass) return false

        other as Toilet

        if(this.name == other.name && this.latitude == other.latitude && this.longitude == other.longitude) return true

        return false
    }

    override fun hashCode(): Int {
        return name.hashCode() + latitude.hashCode() + longitude.hashCode()
    }

}