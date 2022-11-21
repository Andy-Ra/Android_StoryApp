package com.andyra.storyapp.preference

import android.content.Context
import android.util.Log

class SessionPreference(context: Context) {
    private val preferences = context.getSharedPreferences(EXTRA_TOKEN_ID, Context.MODE_PRIVATE)

    fun setSession(mToken: String) {
        val mEditValue = preferences.edit()
        mEditValue.putString(EXTRA_TOKEN_ID, mToken)
        Log.e(this@SessionPreference.toString(), "ara session value $mToken")
        mEditValue.apply()
    }

    fun getSession(): String? {
        return preferences.getString(EXTRA_TOKEN_ID, "")
    }

    companion object {
        private const val EXTRA_TOKEN_ID = "token_id"
    }
}
