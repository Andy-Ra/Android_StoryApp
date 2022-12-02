package com.andyra.storyapp.repository

import com.andyra.storyapp.api.ApiConfig
import com.andyra.storyapp.data.remote.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository {
    suspend fun getListStoryFromRemote(mTokenId: String): Response<StoryResponse> =
        ApiConfig.getApiServices().getListStory(mTokenId)

    suspend fun postStoryToRemote(mTokenId: String, mPhoto: MultipartBody.Part, mDescription: RequestBody): Response<StoryResponse> =
        ApiConfig.getApiServices().postStory(mTokenId, mPhoto, mDescription)

    suspend fun getUserLocation(mTokenId: String, mLocation: Int): Response<StoryResponse> =
        ApiConfig.getApiServices().getListStoryWithLocation(mTokenId, mLocation)
}