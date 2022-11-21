package com.andyra.storyapp.ui.views.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
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

        mBinding.btnRegisterLogin.setOnClickListener {
            intent(LoginActivity::class.java)
            finish()
        }

        mBinding.btnRegisterRegister.setOnClickListener {
            validation()
        }
        removeSpacingPassword()
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

    private fun removeSpacingPassword() {
        mBinding.apply {
            edRegisterPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val regPass = edRegisterPassword.editableText.toString()
                    regisPass = regPass.replace(" ", "")
                    if (regPass != regisPass) {
                        edRegisterPassword.setText(regisPass)
                    }

                    val passPosition = edRegisterPassword.editableText.length
                    edRegisterPassword.setSelection(passPosition)
                }
            })

        }
    }
}