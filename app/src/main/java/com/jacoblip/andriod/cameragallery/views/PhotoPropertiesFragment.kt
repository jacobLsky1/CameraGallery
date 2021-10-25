package com.jacoblip.andriod.cameragallery.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.databinding.PhotoProportiesBinding


class PhotoPropertiesFragment(var photo: Photo):Fragment(), OnMapReadyCallback {


    private var mapView: MapView? = null
    lateinit var binding:PhotoProportiesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PhotoProportiesBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        view.apply {
            mapView = binding.photoMapView as MapView
            mapView!!.onCreate(savedInstanceState)
            mapView!!.onResume()
            mapView!!.getMapAsync(this@PhotoPropertiesFragment)

            binding.timeStampTV.text =photo.date.toString()

            binding.shareButton.setOnClickListener {
                sharePhoto(photo)
            }
        }
        return view
    }

    fun sharePhoto(photo: Photo){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, photo.uri.toUri())
        startActivity(Intent.createChooser(intent, "Share"))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(map: GoogleMap?) {
        var latLng = LatLng(photo.lat, photo.lng)
        map!!.addMarker(MarkerOptions().position(latLng).title("your photo was taken here"))
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F));
        // Zoom in, animating the camera.
        map!!.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map!!.animateCamera(CameraUpdateFactory.zoomTo(15F), 1000, null);
    }

    companion object{
        fun newInstance(photo: Photo):PhotoPropertiesFragment{
            return PhotoPropertiesFragment(photo)
        }
    }
}