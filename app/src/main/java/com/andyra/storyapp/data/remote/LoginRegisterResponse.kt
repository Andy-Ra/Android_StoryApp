package com.andyra.storyapp.data.remote

import android.os.Parcelable
import com.andyra.storyapp.data.remote.login.LoginResultResponse
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRegisterResponse(
    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResultResponse? = null,
) : Parcelable
