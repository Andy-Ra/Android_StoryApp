package com.andyra.storyapp.ui.auth

import com.andyra.storyapp.data.remote.story.ListStoryResponse

interface LocationAuthentication {
    fun onSuccess(mListStoryResponse: ArrayList<ListStoryResponse>)
}
