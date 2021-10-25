package com.jacoblip.andriod.cameragallery.utilties

import androidx.lifecycle.MutableLiveData

class Util {
    companion object{
        var hasInternet:MutableLiveData<Boolean> = MutableLiveData(true)
    }
}