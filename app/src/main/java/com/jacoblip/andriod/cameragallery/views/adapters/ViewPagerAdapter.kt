package com.jacoblip.andriod.cameragallery.views.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.jacoblip.andriod.cameragallery.R
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.views.PhotoFragment
import java.util.*


class ViewPagerAdapter(var context: Context, var photos: Array<Photo>,var callbacks:PhotoFragment.Callbacks): PagerAdapter() {
    var layoutInflater: LayoutInflater =context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return  photos.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = layoutInflater.inflate(R.layout.view_pager_photo, container, false)

        // referencing the image view from the item.xml file

        // referencing the image view from the item.xml file
        val imageView: ImageView = itemView.findViewById<View>(R.id.viewPagerImage) as ImageView

        // setting the image in the imageView

        // setting the image in the imageView
        imageView.setImageBitmap(getBitmapFromByteStream(photos.get(position).photo!!))
        imageView.setOnClickListener {
            callbacks.onPhotoClicked(photos.get(position))
        }
        // Adding the View

        // Adding the View
        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    fun getBitmapFromByteStream(byts: ByteArray): Bitmap {
        val bmp = BitmapFactory.decodeByteArray(byts, 0, byts.size)
        return bmp
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}