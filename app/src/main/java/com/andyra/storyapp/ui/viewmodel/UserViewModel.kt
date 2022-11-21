package com.andyra.storyapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.login.LoginRequest
import com.andyra.storyapp.data.remote.register.RegisterRequest
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.repository.UserRepository
import com.andyra.storyapp.ui.auth.UserAuthentication
import com.google.gson.Gson
import kotlinx.coroutines.launch

class UserViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    private val mUserRepo = UserRepository()
    private val mSessionPreference = SessionPreference(mApplication)

    private val mMutableLoginRegisterResponse = MutableLiveData<LoginRegisterResponse>()
    private val mLDLoginRegisterResponse: LiveData<LoginRegisterResponse> =
        mMutableLoginRegisterResponse

    var mUserAuthentication: UserAuthentication? = null

    fun userRegister(mRegisterRequest: RegisterRequest) = viewModelScope.launch {
        mUserRepo.postUserRegisterToRemote(mRegisterRequest).run {
            if (this.isSuccessful) {
                mMutableLoginRegisterResponse.value = this.body()
                mUserAuthentication?.onSuccess(mLDLoginRegisterResponse)
            } else {
                val mErrorResponse = Gson().fromJson(
                    this.errorBody()!!.charStream(),
                    LoginRegisterResponse::class.java
                )
                mMutableLoginRegisterResponse.value = mErrorResponse
                mUserAuthentication?.onFailure(mLDLoginRegisterResponse)
            }
        }
    }

    fun userLogin(mLoginRequest: LoginRequest) = viewModelScope.launch {
        mUserRepo.getUserLoginFromRemote(mLoginRequest).run {
            if (this.isSuccessful) {
                mMutableLoginRegisterResponse.value = this.body()

                mSessionPreference.setSession(mMutableLoginRegisterResponse.value!!.loginResult!!.token.toString())
                mUserAuthentication?.onSuccess(mLDLoginRegisterResponse)
            } else {
                val mErrorResponse = Gson().fromJson(
                    this.errorBody()!!.charStream(),
                    LoginRegisterResponse::class.java
                )
                mMutableLoginRegisterResponse.value = mErrorResponse
                mUserAuthentication?.onFailure(mLDLoginRegisterResponse)
            }
        }
    }
}