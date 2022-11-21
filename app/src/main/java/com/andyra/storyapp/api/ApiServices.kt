package com.andyra.storyapp.api

import com.andyra.storyapp.data.remote.login.LoginRequest
import com.andyra.storyapp.data.remote.register.RegisterRequest
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @POST("register")
    suspend fun postUserRegister(
        @Body RegisterRequest: RegisterRequest
    ): Response<LoginRegisterResponse>


    @POST("login")
    suspend fun getUserLogin(
        @Body loginRequest: LoginRequest
    ): Response<LoginRegisterResponse>

    @GET("stories")
    suspend fun getListStory(
        @Header ("Authorization") tokenId :String
    ): Response<StoryResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header ("Authorization") tokenId :String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Response<StoryResponse>
}
