package com.wisekrakr.wisemessenger.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.wisekrakr.wisemessenger.activity.BaseActivity
import com.wisekrakr.wisemessenger.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.ActivityLoginMobileBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Actions.ClassActions.returnToActivityWithFlags
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginMobileActivity : BaseActivity<ActivityLoginMobileBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityLoginMobileBinding =
        ActivityLoginMobileBinding::inflate

    private lateinit var loginInputsArray: Array<EditText>
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun setup() {

        binding.btnSendCodeLoginMobile.setOnClickListener {
            loginInputsArray = arrayOf(
                binding.etPhoneNumberLoginMobile
            )

            sendVerificationCode()
        }

        binding.btnResetLoginMobile.setOnClickListener {
            visibilityViews(false)
        }

        binding.btnLoginMobile.setOnClickListener {
            loginInputsArray = arrayOf(
                binding.etVerificationCodeLoginMobile
            )

            login()
        }
    }

    private fun login(){
        if(isNotEmpty(loginInputsArray)){

            val verificationCode: String = binding.etVerificationCodeLoginMobile.text.toString()

            val credential = PhoneAuthProvider.getCredential(
                storedVerificationId,
                verificationCode
            )
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun sendVerificationCode() {

        if(isNotEmpty(loginInputsArray)){

            val phoneNumber: String = binding.etPhoneNumberLoginMobile.text.toString()

            launch {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this@LoginMobileActivity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }

            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    makeToast("Verification successful")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    makeToast("Invalid phone number \n Please enter a valid phone number with country code.")
                    Log.w(ACTIVITY_TAG, "Verification failed: ${e.cause}")

                    visibilityViews(false)

                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d(ACTIVITY_TAG, "Verification code was send to user")

                    storedVerificationId = verificationId
                    resendToken = token

                    visibilityViews(true)

                }
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d(ACTIVITY_TAG, "Sign in with phone number was successful")
                    makeToast("Sign in with phone number was successful")

                    startActivity(Intent(this@LoginMobileActivity, HomeActivity::class.java))
                    finish()

                } else {
                    Log.w(ACTIVITY_TAG, "Sign in with phone number failed!")
                    makeToast("Sign in with phone number has failed!")

                }
            }
    }

    private fun visibilityViews(sendVerification: Boolean){
        if(sendVerification){
            binding.btnSendCodeLoginMobile.visibility = INVISIBLE
            binding.btnLoginMobile.visibility = VISIBLE
            binding.etPhoneNumberLoginMobile.visibility = INVISIBLE
            binding.etVerificationCodeLoginMobile.visibility = VISIBLE
        }else{
            binding.btnSendCodeLoginMobile.visibility = VISIBLE
            binding.btnLoginMobile.visibility = INVISIBLE
            binding.etPhoneNumberLoginMobile.visibility = VISIBLE
            binding.etVerificationCodeLoginMobile.visibility = INVISIBLE
        }
    }

    override fun supportBar() {
        actionBar?.hide()
    }

    // Disable the menu on this activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}