package com.andyra.storyapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andyra.storyapp.R
import com.andyra.storyapp.di.Injection


class ViewModelFactory (private val mContext: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(mContext)) as T
        }
        throw IllegalArgumentException(StringBuilder(R.string.unknown_viewmodel).toString())
    }
}
