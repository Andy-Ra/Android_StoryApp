package com.andyra.storyapp.ui.viewmodel

import androidx.lifecycle.*
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.login.LoginRequest
import com.andyra.storyapp.data.remote.register.RegisterRequest
import com.andyra.storyapp.repository.UserRepository
import com.andyra.storyapp.ui.auth.UserAuthentication
import com.google.gson.Gson
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val mUserRepo = UserRepository()

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