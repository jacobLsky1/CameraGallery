package com.jacoblip.andriod.cameragallery.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.data.services.MainViewModel
import com.jacoblip.andriod.cameragallery.databinding.FragmentRvBinding
import com.jacoblip.andriod.cameragallery.views.adapters.PhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val MY_PERMISSIONS_REQUEST_LOCATION = 99;
private const val REQUEST_IMAGE_CAPTURE = 1

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class PhotoRVFragment() : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var photoRV:RecyclerView
    private lateinit var binding: FragmentRvBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null


    interface Callbacks {
        fun onPhotoSelected(position:Int,photo:Photo)
    }

    private var callbacks: Callbacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRvBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        photoRV = binding.photoRV
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager?
        setUpObservers()
        checkforLocationPermission()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            binding.fab.setOnClickListener {
                dispatchTakePictureIntent()
            }
            photoRV.layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    fun setUpObservers(){
        viewModel.photos.observe(viewLifecycleOwner){
            photoRV.adapter = PhotoAdapter(it,callbacks!!)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(imageBitmap: Bitmap) {
        var loc :Location? = null
        var canAccess = checkforLocationPermission()

        if (canAccess) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Toast.makeText(requireContext(),"location:${location!!.latitude} , ${location!!.longitude}",Toast.LENGTH_LONG).show()
                    loc = location
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"could not get location",Toast.LENGTH_LONG).show()
                }

        }else{
            Toast.makeText(requireContext(),"no permission to access location",Toast.LENGTH_LONG).show()
        }
        makePhotoObject(imageBitmap,loc)
    }


    fun makePhotoObject(imageBitmap: Bitmap, location: Location?){
        if(location==null){
            Toast.makeText(requireContext(),"the photo is without a location",Toast.LENGTH_SHORT).show()
        }
        viewModel.makePhotoObject(imageBitmap,location)
    }

    private fun checkforLocationPermission():Boolean{
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Grant permisson")
                    .setMessage("should this app be able to access your location?")
                    .setPositiveButton("YES",
                        DialogInterface.OnClickListener { dialogInterface, i -> //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        })
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            getLocation(imageBitmap)
        }
    }




    companion object{
        fun newInstance():PhotoRVFragment {
           return PhotoRVFragment()
        }
    }
}
