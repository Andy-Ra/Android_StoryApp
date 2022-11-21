package com.andyra.storyapp.util

import android.content.Context
import android.content.Intent
import android.widget.Toast


fun Context.toast(mMessage: String) {
    Toast.makeText(this, mMessage, Toast.LENGTH_SHORT).show()
}

fun Context.intent(mClass: Class<*>) {
    val mIntent = Intent(this, mClass)
    startActivity(mIntent)
}

