package com.andyra.storyapp.util

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import com.andyra.storyapp.R

class LoadingDialog(private val mActivity: Activity) {
    private lateinit var mDialog: AlertDialog

    fun isLoading(mLoading: Boolean) {
        val mInflater = mActivity.layoutInflater.inflate(R.layout.activity_progress_bar, null)
        val mBuilder = AlertDialog.Builder(mActivity)
        mBuilder.setView(mInflater)
        mBuilder.setCancelable(false)

        if (mLoading) {
            mDialog = mBuilder.create()
            mDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            mDialog.show()
        } else {
            mDialog.dismiss()
        }
    }
}