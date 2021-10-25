package com.jacoblip.andriod.cameragallery.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.jacoblip.andriod.cameragallery.R
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.data.services.MainViewModel
import com.jacoblip.andriod.cameragallery.databinding.FragmentPhotoBinding
import com.jacoblip.andriod.cameragallery.views.adapters.ViewPagerAdapter

class PhotoFragment(var position:Int,var photo:Photo):Fragment() {

    lateinit var binding: FragmentPhotoBinding
    lateinit var viewPager:ViewPager
    lateinit var viewModel: MainViewModel

    interface Callbacks {
        fun onPhotoClicked(photo:Photo)
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        view.apply {
            viewPager = binding.viewPagerMain
        }
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpObservers()
        return view
    }

    fun setUpObservers(){
        viewModel.photos.observe(viewLifecycleOwner){
            if(it!=null){
                viewPager.adapter = ViewPagerAdapter(requireContext(),it,callbacks!!)
                viewPager.currentItem = position
            }
        }
    }

    companion object{
        fun newInstance(position:Int,photo: Photo):PhotoFragment {
            return PhotoFragment(position,photo)
        }
    }
}