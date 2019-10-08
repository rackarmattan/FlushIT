package com.example.flushit.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Model class for a user.
 */
class User() :Parcelable{
    var email: String? = null
    var permissionLevel:String? = null
    var uid: String? = null
    var ratedToilets: MutableList<Toilet> = mutableListOf()

    private constructor(parcel: Parcel) : this() {
        email = parcel.readString()
        permissionLevel = parcel.readString()
        uid = parcel.readString()
        mutableListOf<Toilet>().apply { parcel.readList(this, Toilet::class.java.classLoader) }
    }

    constructor(_email:String, _permissionLevel:String, _uid:String): this(){
        email = _email
        permissionLevel = _permissionLevel
        uid = _uid
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.let {
            it.writeString(email)
            it.writeString(permissionLevel)
            it.writeString(uid)
            it.writeList(ratedToilets)
        }
    }

    override fun describeContents() = 0

}