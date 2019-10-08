package com.example.flushit.model

import android.os.Parcel
import android.os.Parcelable

class Rating() : Parcelable {

    var grade: Float = 0f
    var user: User? = null
    var toilet: Toilet? = null

    private constructor(parcel: Parcel) : this() {
        grade = parcel.readFloat()
        user = parcel.readParcelable(User::class.java.classLoader)
        toilet = parcel.readParcelable(Toilet::class.java.classLoader)
    }

    constructor(_grade: Float, _user: User, _toilet: Toilet) : this(){
        grade = _grade
        user = _user
        toilet = _toilet
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(grade)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(toilet, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rating> {
        override fun createFromParcel(parcel: Parcel): Rating {
            return Rating(parcel)
        }

        override fun newArray(size: Int): Array<Rating?> {
            return arrayOfNulls(size)
        }
    }
}