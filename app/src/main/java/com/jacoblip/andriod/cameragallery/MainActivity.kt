package com.jacoblip.andriod.cameragallery

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.jacoblip.andriod.cameragallery.data.models.Photo
import com.jacoblip.andriod.cameragallery.data.services.MainViewModel
import com.jacoblip.andriod.cameragallery.databinding.ActivityMainBinding
import com.jacoblip.andriod.cameragallery.utilties.WifiReceiver
import com.jacoblip.andriod.cameragallery.views.PhotoFragment
import com.jacoblip.andriod.cameragallery.views.PhotoPropertiesFragment
import com.jacoblip.andriod.cameragallery.views.PhotoRVFragment
import dagger.hilt.android.AndroidEntryPoint
import io.realm.Realm
import io.realm.RealmConfiguration


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),PhotoRVFragment.Callbacks,PhotoFragment.Callbacks {

    val viewModel: MainViewModel by viewModels()
    lateinit var wifiReceiver: WifiReceiver
    lateinit var fragment : Fragment
    private lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRealm()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view  = binding.root
        setContentView(view)
        setUpServices()
        fragment = PhotoRVFragment.newInstance()
        setTheFragment(fragment)
    }

    fun initRealm(){
        Realm.init(this)
        var realmConfiguration = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfiguration)

        /*
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()

         */

    }

    fun setTheFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun setUpServices(){
        wifiReceiver = WifiReceiver()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiReceiver)
    }

    override fun onPhotoSelected(position:Int,photo:Photo) {
        fragment = PhotoFragment.newInstance(position,photo)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPhotoClicked(photo: Photo) {
        fragment = PhotoPropertiesFragment.newInstance(photo)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}