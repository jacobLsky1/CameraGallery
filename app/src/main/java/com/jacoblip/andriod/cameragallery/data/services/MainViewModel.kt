package com.jacoblip.andriod.cameragallery.data.services

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jacoblip.andriod.cameragallery.data.models.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

@HiltViewModel
class MainViewModel @Inject constructor(
    private val context: Context, private val repository: Repository,
    private val firestore: FirebaseFirestore, private val firebaseStorage: FirebaseStorage
)
    :ViewModel() {

    private var _photos=MutableLiveData<Array<Photo>>()
    var photos:LiveData<Array<Photo>> = _photos

        init {
           readPhotoFromInternal()
        }


    fun bitMapToByteArray(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()

        return byteArray
    }

    //Make sure to call this function on a worker thread, else it will block main thread
    fun saveImageInQ(photo: Photo):Uri? {

        val filename = photo.photoFileNamed
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = context.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { getBitmapFromByteStream(photo.photo!!).compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!,contentValues, null, null)

        return imageUri

    }

    fun saveImageInLegacy(photo: Photo):Uri?{

        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, photo.photoFileNamed)
        val fos = FileOutputStream(image)

        fos?.use {getBitmapFromByteStream(photo.photo!!).compress(Bitmap.CompressFormat.JPEG, 100, it)}
        var uri = Uri.fromFile(image)
        return uri
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhotoObject(image: Bitmap, location: Location?){


        doAsync {

            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)

            val photo = Photo(
                UUID.randomUUID().toString(),
                 formatted,
                location!!.latitude,
                location!!.longitude,
                bitMapToByteArray(image),

            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
               var uri =  saveImageInQ(photo)
                photo.uri = uri.toString()
            }
            else {
                var uri = saveImageInLegacy(photo)
                photo.uri = uri.toString()
            }

            uiThread {

                repository.storePhotoFireBase(firestore, firebaseStorage, photo, context)
                repository.storePhotoToInternalMemory(photo)
                readPhotoFromInternal()
            }
        }


    }

    fun getBitmapFromByteStream(byts: ByteArray):Bitmap{
        val bmp = BitmapFactory.decodeByteArray(byts, 0,byts.size)
        return bmp
    }

    fun readPhotoFromInternal(){
        _photos.postValue(repository.getPhotosFromStorage())
    }

}