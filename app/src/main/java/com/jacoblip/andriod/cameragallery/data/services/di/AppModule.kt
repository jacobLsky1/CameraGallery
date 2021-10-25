package com.jacoblip.andriod.cameragallery.data.services.di

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jacoblip.andriod.cameragallery.data.services.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRepository() = Repository()

    @Singleton
    @Provides
    fun getLocationManager( @ApplicationContext context: Context) = context.getSystemService(LOCATION_SERVICE) as android.location.LocationManager


    @Singleton
    @Provides
    fun getContext( @ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun getFireBaseInstance() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun getFirebaseStorage() = FirebaseStorage.getInstance()


}