package com.andyra.storyapp.ui.views.story

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.databinding.ActivityUserLocationBinding
import com.andyra.storyapp.databinding.CustomUserLocationBarLayoutBinding
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.ui.auth.LocationAuthentication
import com.andyra.storyapp.ui.viewmodel.StoryViewModel
import com.andyra.storyapp.ui.viewmodel.ViewModelFactory
import com.andyra.storyapp.util.LoadingDialog
import com.andyra.storyapp.util.toast

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class UserLocationActivity : AppCompatActivity(), OnMapReadyCallback, LocationAuthentication {

    private lateinit var mGMap: GoogleMap
    private lateinit var mBinding: ActivityUserLocationBinding
    private lateinit var mActionBinding: CustomUserLocationBarLayoutBinding
    private lateinit var mSessionPreference: SessionPreference


    private val mStoryVM: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    private val mLoading = LoadingDialog(this)

    private val mListStory = ArrayList<ListStoryResponse>()
    private val mLocation = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUserLocationBinding.inflate(layoutInflater)
        mActionBinding = CustomUserLocationBarLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = mActionBinding.root

        mStoryVM.mLocationAuthentication = this
        mSessionPreference = SessionPreference(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.frag_story_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        actionBarButton()
    }

    override fun onCreateOptionsMenu(mMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_maps, mMenu)
        return true
    }

    override fun onOptionsItemSelected(mItem: MenuItem): Boolean {
        return when (mItem.itemId) {
            R.id.normal_type -> {
                mGMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mGMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mGMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mGMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(mItem)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGMap = googleMap
        mGMap.uiSettings.isCompassEnabled = true
        mGMap.uiSettings.isZoomControlsEnabled = true

        getMapPermission()
        showUserLocation()
    }

    override fun onSuccess(mListStoryResponse: ArrayList<ListStoryResponse>) {
        mListStory.clear()
        for (mStory in mListStoryResponse) {
            val mLatLng = LatLng(mStory.lat!!, mStory.lon!!)
            mGMap.addMarker(
                MarkerOptions()
                    .position(mLatLng)
                    .snippet(mStory.description)
                    .title(mStory.name)
            )
        }
        mLoading.isLoading(false)
    }


    private fun showUserLocation() {
        val mTokenID = StringBuilder("Bearer ").append(mSessionPreference.getSession()).toString()
        mLoading.isLoading(true)
        mStoryVM.getLocation(mTokenID, mLocation)
    }

    private val mLocationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getMapPermission()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getMapPermission()
                }
                else -> {
                    finish()
                    toast(getString(R.string.permission_denied))
                }
            }
        }
    }

    private fun getMapPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGMap.isMyLocationEnabled = true
        } else {
            mLocationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun actionBarButton() {
        mActionBinding.imbCloseDetailStory.setOnClickListener {
            finish()
        }
    }

}