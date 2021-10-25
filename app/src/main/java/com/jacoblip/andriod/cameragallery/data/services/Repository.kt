package com.jacoblip.andriod.cameragallery.data.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jacoblip.andriod.cameragallery.data.models.FireBasePhotoData
import com.jacoblip.andriod.cameragallery.data.models.Photo
import io.realm.Realm
import java.io.*


class Repository {


    fun storePhotoFireBase(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        photo: Photo,
        context: Context
    ){
        val photoBitMap = photo.photo
        val firebasePhoto = FireBasePhotoData(
            photo.id.toString(),
            photo.date,
            photo.lat,
            photo.lng,
            photo.photoFileNamed
        )
        firestore.collection("photos").document(photo.id.toString()).set(firebasePhoto).addOnSuccessListener {
            Log.i("ViewModel", "uploaded your photo")

        }
        firebaseStorage.reference.child(photo.id.toString()).putFile(getImageUri(context, photo)!!)
    }

    fun getImageUri(inContext: Context, photo: Photo): Uri? {
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            getBitmapFromByteStream(photo.photo!!),
            photo.photoFileNamed,
            null
        )
        return Uri.parse(path)
    }

    fun getBitmapFromByteStream(byts: ByteArray):Bitmap{
        val bmp = BitmapFactory.decodeByteArray(byts, 0,byts.size)
        return bmp
    }

    fun storePhotoToInternalMemory(photo: Photo){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insert(photo)
        realm.commitTransaction()

    }

    fun getPhotosFromStorage():Array<Photo>?{
        var photos:Array<Photo>? = null
        val realm = Realm.getDefaultInstance()
        photos = realm.where(Photo::class.java).findAll().toTypedArray()
        return photos
    }
}