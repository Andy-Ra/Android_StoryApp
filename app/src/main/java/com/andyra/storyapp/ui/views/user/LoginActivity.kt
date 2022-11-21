package com.andyra.storyapp.ui.views.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
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
        mBinding.btnLoginCreate.setOnClickListener {
            intent(RegisterActivity::class.java)
            finish()
        }

        mBinding.btnLoginLogin.setOnClickListener {
            validation()
        }
        removeSpacingPassword()
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
            loginPass = edLoginPassword.editableText.toString().replace(" ", "")
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

    private fun removeSpacingPassword() {
        mBinding.apply {
            edLoginPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val mLoginPass = edLoginPassword.editableText.toString()
                    loginPass = mLoginPass.replace(" ", "")
                    if (mLoginPass != loginPass) {
                        edLoginPassword.setText(loginPass)
                    }

                    val passPosition = edLoginPassword.editableText.length
                    edLoginPassword.setSelection(passPosition)
                }
            })

        }
    }


}