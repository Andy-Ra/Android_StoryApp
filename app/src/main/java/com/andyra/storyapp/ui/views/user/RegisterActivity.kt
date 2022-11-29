package com.andyra.storyapp.ui.views.user

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.LoginRegisterResponse
import com.andyra.storyapp.data.remote.register.RegisterRequest
import com.andyra.storyapp.databinding.ActivityRegisterBinding
import com.andyra.storyapp.ui.auth.UserAuthentication
import com.andyra.storyapp.ui.viewmodel.UserViewModel
import com.andyra.storyapp.util.LoadingDialog
import com.andyra.storyapp.util.intent
import com.andyra.storyapp.util.toast

class RegisterActivity : AppCompatActivity(), UserAuthentication {
    private lateinit var mBinding: ActivityRegisterBinding
    private val mUserVM: UserViewModel by viewModels()
    private val mLoading = LoadingDialog(this)

    private var regisFName = ""
    private var regisLName = ""
    private var regisEmail = ""
    private var regisPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mUserVM.mUserAuthentication = this
        getAnimation()
        mBinding.btnRegisterLogin.setOnClickListener {
            intent(LoginActivity::class.java)
            finish()
        }

        mBinding.btnRegisterRegister.setOnClickListener {
            validation()
        }
    }

    override fun onSuccess(mLoginRegisterResponse: LiveData<LoginRegisterResponse>) {
        val responseResult = mLoginRegisterResponse.value
        toast(responseResult!!.message)

        mLoading.isLoading(false)
        intent(LoginActivity::class.java)
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
            regisFName = edRegisterFirstName.editableText.toString().replace(" ", "")
            regisLName = edRegisterLastName.editableText.toString().replace(" ", "")
            regisEmail = edRegisterEmail.editableText.toString()
            regisPass = edRegisterPassword.editableText.toString()

            if (regisFName.isBlank()) {
                edRegisterFirstName.error = getString(R.string.required_field)
                validForm = false
            }

            if (regisLName.isBlank()) {
                edRegisterLastName.error = getString(R.string.required_field)
                validForm = false
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(regisEmail).matches()) {
                edRegisterEmail.error = getString(R.string.invalid_email)
                validForm = false
            }
            if (regisPass.length < 6) {
                edRegisterPassword.error = getString(R.string.invalid_password)
                validForm = false
            }
            regisProcess(validForm)
        }
    }

    private fun regisProcess(mValid: Boolean) {
        if (mValid) {
            mLoading.isLoading(true)
            mUserVM.userRegister(
                RegisterRequest(
                    StringBuilder(regisFName).append(" ").append(regisLName).toString(),
                    regisEmail,
                    regisPass
                )
            )
        }
    }

    private fun getAnimation() {
        ObjectAnimator.ofFloat(mBinding.tvRegisterTitle, View.TRANSLATION_X, 0f, 60f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val lynameAnimation =
            ObjectAnimator.ofFloat(mBinding.lyRegisterName, View.ALPHA, 1f).setDuration(600)
        val emailAnimation =
            ObjectAnimator.ofFloat(mBinding.lyRegisterEmail, View.ALPHA, 1f).setDuration(600)
        val passwordAnimation =
            ObjectAnimator.ofFloat(mBinding.lyRegisterPassword, View.ALPHA, 1f).setDuration(600)
        val btnCreateAnimation =
            ObjectAnimator.ofFloat(mBinding.btnRegisterRegister, View.ALPHA, 1f).setDuration(600)
        val btnLoginAnimation =
            ObjectAnimator.ofFloat(mBinding.btnRegisterLogin, View.ALPHA, 1f).setDuration(600)
        val txtOrAnimation =
            ObjectAnimator.ofFloat(mBinding.lyRegisterOr, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(
                lynameAnimation,
                emailAnimation,
                passwordAnimation,
                btnCreateAnimation,
                txtOrAnimation,
                btnLoginAnimation
            )
            start()
        }
    }

}