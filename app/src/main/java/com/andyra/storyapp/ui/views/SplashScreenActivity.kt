package com.andyra.storyapp.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.andyra.storyapp.BuildConfig
import com.andyra.storyapp.R
import com.andyra.storyapp.databinding.ActivitySplashScreenBinding
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.ui.views.user.LoginActivity
import com.andyra.storyapp.util.intent

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySplashScreenBinding
    private lateinit var mSessionPreference: SessionPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mSessionPreference = SessionPreference(this)
        loadingSplash()

    }

    private fun loadingSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, delayTime)
        mBinding.tvSVersion.text =
            StringBuilder(getString(R.string.version_title)).append(" ")
                .append(BuildConfig.VERSION_NAME)
    }

    private fun checkSession() {
        val mToken = mSessionPreference.getSession().toString()
        if (mToken == "") {
            intent(LoginActivity::class.java)
            finish()
        } else {
            intent(MainActivity::class.java)
            finish()
        }
    }

    companion object {
        private const val delayTime: Long = 4000
    }
}