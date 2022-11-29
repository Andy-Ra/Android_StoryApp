package com.andyra.storyapp.ui.views.user

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.login.LoginRequest
import com.andyra.storyapp.databinding.ActivityLoginBinding
import com.andyra.storyapp.ui.auth.UserAuthentication
import com.andyra.storyapp.ui.viewmodel.UserViewModel
import com.andyra.storyapp.ui.views.MainActivity
import com.andyra.storyapp.util.LoadingDialog
import com.andyra.storyapp.util.intent
import com.andyra.storyapp.util.toast


class LoginActivity : AppCompatActivity(), UserAuthentication {
    private lateinit var mBinding: ActivityLoginBinding

    private val mUserVM: UserViewModel by viewModels()
    private val mLoading = LoadingDialog(this)
    private var loginEmail = ""
    private var loginPass = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        mUserVM.mUserAuthentication = this

        getAnimation()
        mBinding.btnLoginCreate.setOnClickListener {
            intent(RegisterActivity::class.java)
            finish()
        }

        mBinding.btnLoginLogin.setOnClickListener {
            validation()
        }
    }


    override fun onSuccess(mLoginRegisterResponse: LiveData<LoginRegisterResponse>) {
        mLoading.isLoading(false)
        val responseResult = mLoginRegisterResponse.value
        toast(responseResult!!.message)


        intent(MainActivity::class.java)
        finish()
    }

    override fun onFailure(mLoginRegisterResponse: LiveData<LoginRegisterResponse>) {
        mLoading.isLoading(false)
        val responseResult = mLoginRegisterResponse.value
        toast(responseResult!!.message)
    }

    private fun validation() {
        var validForm = true
        mBinding.apply {
            loginEmail = edLoginEmail.editableText.toString()
            loginPass = edLoginPassword.editableText.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
                edLoginEmail.error = getString(R.string.invalid_email)
                validForm = false
            }
            if (loginPass.length < 6) {
                edLoginPassword.error = getString(R.string.invalid_password)
                validForm = false
            }
            loginProcess(validForm)
        }
    }

    private fun loginProcess(mValid: Boolean) {
        if (mValid) {
            mLoading.isLoading(true)
            mUserVM.userLogin(
                LoginRequest(
                    loginEmail,
                    loginPass
                )
            )
        }

    }

    private fun getAnimation() {
        ObjectAnimator.ofFloat(mBinding.tvLoginTitle, View.TRANSLATION_X, 0f, 60f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val emailAnimation =
            ObjectAnimator.ofFloat(mBinding.lyLoginEmail, View.ALPHA, 1f).setDuration(600)
        val passwordAnimation =
            ObjectAnimator.ofFloat(mBinding.lyLoginPassword, View.ALPHA, 1f).setDuration(600)
        val btnLoginAnimation =
            ObjectAnimator.ofFloat(mBinding.btnLoginLogin, View.ALPHA, 1f).setDuration(600)
        val btnCreateAnimation =
            ObjectAnimator.ofFloat(mBinding.btnLoginCreate, View.ALPHA, 1f).setDuration(600)
        val txtOrAnimation =
            ObjectAnimator.ofFloat(mBinding.lyLoginOr, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(
                emailAnimation,
                passwordAnimation,
                btnLoginAnimation,
                txtOrAnimation,
                btnCreateAnimation
            )
            start()
        }
    }

}