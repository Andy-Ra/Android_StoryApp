package com.andyra.storyapp.data.remote.story

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class StoryResponse (
    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    val listStory: ArrayList<ListStoryResponse>? = null,
):Parcelable