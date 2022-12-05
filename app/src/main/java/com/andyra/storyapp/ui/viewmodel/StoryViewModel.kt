package com.andyra.storyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.data.remote.story.StoryResponse
import com.andyra.storyapp.repository.StoryRepository
import com.andyra.storyapp.ui.auth.LocationAuthentication
import com.andyra.storyapp.ui.auth.PostAuthentication
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val mStoryRepo: StoryRepository) : ViewModel() {
    private val mMutableStoryResponse = MutableLiveData<StoryResponse>()

    private val mLDStoryResponse: LiveData<StoryResponse> = mMutableStoryResponse

    var mLocationAuthentication: LocationAuthentication? = null
    var mPostAuthentication: PostAuthentication? = null


    fun listStory(mTokenId: String): LiveData<PagingData<ListStoryResponse>> =
        mStoryRepo.getListStoryFromRemote(mTokenId).cachedIn(viewModelScope)

    fun postStory(mTokenId: String, mPhoto: MultipartBody.Part, mDescription: RequestBody, mLat: Double?, mLon: Double?)=
        viewModelScope.launch {
            mStoryRepo.postStoryToRemote(mTokenId, mPhoto, mDescription,mLat, mLon).run {
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
                mLocationAuthentication?.onSuccess(mLDStoryResponse.value!!.listStory!!)
            }
        }
    }

}