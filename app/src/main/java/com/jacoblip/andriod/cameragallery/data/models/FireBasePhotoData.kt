package com.jacoblip.andriod.cameragallery.data.models

import android.graphics.Bitmap
import android.location.Location
import java.util.*

data class FireBasePhotoData(
        val id : String = "",
        var date: String = "",
        var lat: Double = 0.0,
        var lng: Double = 0.0,
        var fileName: String
) {
}