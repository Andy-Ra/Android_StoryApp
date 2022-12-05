package com.andyra.storyapp.ui.views.story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.story.StoryResponse
import com.andyra.storyapp.databinding.ActivityPostStoryBinding
import com.andyra.storyapp.databinding.CustomStoryActionBarLayoutBinding
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.ui.auth.PostAuthentication
import com.andyra.storyapp.ui.viewmodel.StoryViewModel
import com.andyra.storyapp.ui.viewmodel.ViewModelFactory
import com.andyra.storyapp.util.LoadingDialog
import com.andyra.storyapp.util.toast
import com.andyra.storyapp.util.uriToFile
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PostStoryActivity : AppCompatActivity(), PostAuthentication {
    private lateinit var mBinding: ActivityPostStoryBinding
    private lateinit var mActionBinding: CustomStoryActionBarLayoutBinding
    private lateinit var mSessionPreference: SessionPreference
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mStoryVM: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }
    private val mLoading = LoadingDialog(this)

    private var mDescription = ""
    private var imageStoryFile: File? = null
    private var myLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPostStoryBinding.inflate(layoutInflater)
        mActionBinding = CustomStoryActionBarLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = mActionBinding.root

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSessionPreference = SessionPreference(this)
        mStoryVM.mPostAuthentication = this

        mBinding.apply {
            btnFormCamera.setOnClickListener {
                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(
                        this@PostStoryActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    showCamera()
                }
            }
            btnFormGallery.setOnClickListener {
                showGallery()
            }
            tbFormLocation.setOnClickListener {
                if (tbFormLocation.isChecked) {
                    tbFormLocation.setBackgroundResource(R.drawable.ic_baseline_wrong_location_24)
                    getCurrentLocation()
                    Log.e(this@PostStoryActivity.toString(), "ara ini menghidupkan")
                } else {
                    tbFormLocation.setBackgroundResource(R.drawable.ic_baseline_add_location_blue_alt_24)

                    myLocation = null
                    Log.e(this@PostStoryActivity.toString(), "ara ini mematikan")
                }
            }
        }

        setProfile()
        actionBarButton()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                toast(getString(R.string.permission_denied))
            } else {
                showCamera()
            }
        }
    }

    override fun onSuccess(mStoryResponse: StoryResponse) {
        mLoading.isLoading(false)
        toast(mStoryResponse.message)

        finish()
    }

    override fun onFailure(mStoryResponse: StoryResponse) {
        mLoading.isLoading(false)
        toast(mStoryResponse.message)
    }

    private fun checkForm() {
        var validForm = true
        mBinding.apply {
            mDescription = edFormDetailStory.editableText.toString()

            if (mDescription == "") {
                edFormDetailStory.error = getString(R.string.fill_this_form)
                validForm = false
            }
            if (imageStoryFile == null) {
                toast(getString(R.string.no_one_image))
            }

        }
        postStory(validForm)
    }

    private fun postStory(mValid: Boolean) {
        if (mValid) {
            uploadImage()
        }

    }

    private fun uploadImage() {
        mLoading.isLoading(true)
        var mLat: Double?  = null
        var mLon: Double? = null
        if(myLocation != null){
            mLat = myLocation?.latitude
            mLon = myLocation?.longitude
        }

        Log.e(this@PostStoryActivity.toString(), "ara Lat $mLat")
        Log.e(this@PostStoryActivity.toString(), "ara Lon $mLon")

        if (imageStoryFile != null) {
            val mTokenID =
                StringBuilder("Bearer ").append(mSessionPreference.getSession()).toString()
            val mDescriptionBody = mDescription.toRequestBody("text/plain".toMediaType())
            val mFile = reduceFileImage(imageStoryFile as File)
            val requestImageFile = mFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val mImageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", mFile.name, requestImageFile
            )

            mStoryVM.postStory(
                mTokenID, mImageMultipart, mDescriptionBody, mLat, mLon
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        when {
            checkLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkLocationPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> mFusedLocationClient.lastLocation.addOnSuccessListener {
                when {
                    it != null -> this.myLocation = LatLng(it.latitude, it.longitude)
                }
            }
            else -> {
                toast(getString(R.string.permission_denied))
                mBinding.tbFormLocation.isChecked = false
            }
        }
    }


    private fun showCamera() {
        val mIntent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(mIntent)
    }

    private fun showGallery() {
        val mIntent = Intent()
        mIntent.action = ACTION_GET_CONTENT
        mIntent.type = "image/*"
        val chooser = Intent.createChooser(mIntent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun setProfile() {
        mBinding.apply {
            Glide.with(this@PostStoryActivity).load(R.drawable.default_user).circleCrop()
                .into(imvFormStory)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(getString(R.string.picture)) as File
            val result = BitmapFactory.decodeFile(myFile.path)

            imageStoryFile = myFile
            mBinding.imvPostStory.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostStoryActivity)

            imageStoryFile = myFile
            mBinding.imvPostStory.setImageURI(selectedImg)
        }
    }

    private fun reduceFileImage(file: File): File {
        val mBitmap = BitmapFactory.decodeFile(file.path)
        var mCompressQuality = 100
        var mStreamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            mBitmap.compress(Bitmap.CompressFormat.JPEG, mCompressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            mStreamLength = bmpPicByteArray.size
            mCompressQuality -= 5
        } while (mStreamLength > 1000000)
        mBitmap.compress(Bitmap.CompressFormat.JPEG, mCompressQuality, FileOutputStream(file))
        return file
    }

    private fun checkLocationPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun actionBarButton() {
        mActionBinding.apply {
            imbCloseAddStory.setOnClickListener {
                finish()
            }
            btnPostStory.setOnClickListener {
                checkForm()
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}