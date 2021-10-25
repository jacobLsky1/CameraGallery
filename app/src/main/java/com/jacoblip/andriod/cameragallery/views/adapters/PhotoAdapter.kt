package com.jacoblip.andriod.cameragallery.views.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.cameragallery.R
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.views.PhotoRVFragment

class PhotoAdapter(var photos:Array<Photo>,var callBacks:PhotoRVFragment.Callbacks):RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(view:View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
       return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rv_photo,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.itemView.apply {
            val imageView = findViewById<ImageView>(R.id.imageView)
            val bmp = BitmapFactory.decodeByteArray(photo.photo, 0,photo.photo!!.size)
            imageView.setImageBitmap(bmp)

            setOnClickListener {
                callBacks.onPhotoSelected(position,photo)
            }
        }
    }

    override fun getItemCount() = photos.size
}