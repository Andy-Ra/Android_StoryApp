package com.andyra.storyapp.di

import android.content.Context
import com.andyra.storyapp.api.ApiConfig
import com.andyra.storyapp.data.local.StoryDatabase
import com.andyra.storyapp.repository.StoryRepository

object Injection {
    fun provideRepository(mContext: Context): StoryRepository {
        val mDatabase = StoryDatabase.getStoryDatabase(mContext)
        val mApiService = ApiConfig.getApiServices()
        return StoryRepository(mApiService, mDatabase)
    }
}