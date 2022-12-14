package com.andyra.storyapp.data.remote.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String,
) : Parcelable