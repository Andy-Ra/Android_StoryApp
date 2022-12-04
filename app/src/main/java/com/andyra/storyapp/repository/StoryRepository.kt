package com.andyra.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.andyra.storyapp.api.ApiConfig
import com.andyra.storyapp.api.ApiServices
import com.andyra.storyapp.data.local.StoryDatabase
import com.andyra.storyapp.data.local.StoryRemoteMediator
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.data.remote.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository(
    val mApiServices: ApiServices,
    val mDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getListStoryFromRemote(mToken: String): LiveData<PagingData<ListStoryResponse>> {
        Log.e(this@StoryRepository.toString(), "ara sampai repo api = $mApiServices")
        Log.e(this@StoryRepository.toString(), "ara sampai repo token = $mToken")
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(mApiServices, mDatabase, mToken),
            pagingSourceFactory = {
                mDatabase.mStoryDao().getLocalStory()
            }
        ).liveData

        Log.e(this@StoryRepository.toString(), "ara sampai pager " +mDatabase.mStoryDao().getLocalStory())
    }

    suspend fun postStoryToRemote( mTokenId: String, mPhoto: MultipartBody.Part, mDescription: RequestBody): Response<StoryResponse> =
        ApiConfig.getApiServices().postStory(mTokenId, mPhoto, mDescription)

    suspend fun getUserLocation(mTokenId: String, mLocation: Int): Response<StoryResponse> =
        ApiConfig.getApiServices().getListStoryWithLocation(mTokenId, mLocation)

}