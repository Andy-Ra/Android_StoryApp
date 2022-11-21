package com.andyra.storyapp.repository

import com.andyra.storyapp.api.ApiConfig
import com.andyra.storyapp.data.remote.login.LoginRequest
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.register.RegisterRequest
import retrofit2.Response

class UserRepository {
    suspend fun postUserRegisterToRemote(mRegisterRequest: RegisterRequest): Response<LoginRegisterResponse> =
        ApiConfig.getApiServices().postUserRegister(mRegisterRequest)

    suspend fun getUserLoginFromRemote(mLoginRequest: LoginRequest): Response<LoginRegisterResponse> =
        ApiConfig.getApiServices().getUserLogin(mLoginRequest)
}

