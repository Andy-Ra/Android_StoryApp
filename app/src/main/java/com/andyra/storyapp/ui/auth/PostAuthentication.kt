package com.andyra.storyapp.ui.auth

import com.andyra.storyapp.data.remote.story.StoryResponse

interface PostAuthentication {
    fun onSuccess(mStoryResponse: StoryResponse)
    fun onFailure(mStoryResponse: StoryResponse)
}