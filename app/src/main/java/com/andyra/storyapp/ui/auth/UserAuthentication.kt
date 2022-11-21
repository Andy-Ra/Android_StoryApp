package com.andyra.storyapp.ui.auth

import androidx.lifecycle.LiveData
import com.andyra.storyapp.data.remote.LoginRegisterResponse

interface UserAuthentication {
    fun onSuccess(mLoginRegisterResponse: LiveData<LoginRegisterResponse>)
    fun onFailure(mLoginRegisterResponse: LiveData<LoginRegisterResponse>)
}