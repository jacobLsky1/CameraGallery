package com.jacoblip.andriod.cameragallery.data.models

import io.realm.RealmObject
import java.io.Serializable
import java.time.LocalTime
import java.util.*

open class Photo(
    var id:String = "",
    var date: String= "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var photo:ByteArray? = null,
    var uri:String = ""
):Serializable,RealmObject() {
    var photoFileNamed = ""
        get() = "IMG_$id.jpg"
}