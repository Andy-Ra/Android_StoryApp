package com.andyra.storyapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andyra.storyapp.data.remote.story.StoryResponse
import com.andyra.storyapp.repository.StoryRepository
import com.andyra.storyapp.ui.auth.PostAuthentication
import com.andyra.storyapp.ui.auth.StoryAuthentication
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    private val mStoryRepo = StoryRepository()
    private val mMutableStoryResponse = MutableLiveData<StoryResponse>()
    private val mLDStoryResponse: LiveData<StoryResponse> = mMutableStoryResponse

    var mStoryAuthentication: StoryAuthentication? = null
    var mPostAuthentication: PostAuthentication? = null

    fun listStory(mTokenId: String) = viewModelScope.launch {
        mStoryRepo.getListStoryFromRemote(mTokenId).run {
            if (this.isSuccessful) {
                mMutableStoryResponse.value = this.body()
                mStoryAuthentication?.onSuccess(mLDStoryResponse.value!!.listStory!!)
            }
        }
    }

    fun postStory(mTokenId: String, mPhoto: MultipartBody.Part, mDescription: RequestBody) =
        viewModelScope.launch {
            mStoryRepo.postStoryToRemote(mTokenId, mPhoto, mDescription).run {
                Log.e(this@StoryViewModel.toString(), "ara tokennn $mTokenId")
                if (this.isSuccessful) {
                    mMutableStoryResponse.value = this.body()
                    mPostAuthentication?.onSuccess(mLDStoryResponse.value!!)
                } else {
                    val mErrorResponse = Gson().fromJson(
                        this.errorBody()!!.charStream(),
                        StoryResponse::class.java
                    )
                    mMutableStoryResponse.value = mErrorResponse
                    mPostAuthentication?.onSuccess(mLDStoryResponse.value!!)
                }
            }
        }


    fun getLocation(mTokenId: String, mLocation: Int) = viewModelScope.launch {
        mStoryRepo.getUserLocation(mTokenId, mLocation).run {
            if (this.isSuccessful) {
                mMutableStoryResponse.value = this.body()
                mStoryAuthentication?.onSuccess(mLDStoryResponse.value!!.listStory!!)
            }
        }
    }

}