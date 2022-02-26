package com.wisekrakr.wisemessenger.components.activity.auth

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import com.google.firebase.messaging.FirebaseMessaging
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.ActivityLoginBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.isRequired
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    private lateinit var signInInputsArray: Array<EditText>

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding =
        ActivityLoginBinding::inflate

    override fun setup() {
        signInInputsArray = arrayOf(binding.emailEditTextLogin, binding.passwordEditTextLogin)

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvNoAccountLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLoginPhone.setOnClickListener {
            startActivity(Intent(this, LoginMobileActivity::class.java))
            finish()
        }
    }

    override fun supportBar() {
        actionBar?.hide()

    }

    private fun login() {
        Log.d(ACTIVITY_TAG, "Signing in... ")
        if (isNotEmpty(signInInputsArray)) {

            signInEmail = binding.emailEditTextLogin.text.toString().trim()
            signInPassword = binding.passwordEditTextLogin.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (!signIn.isSuccessful) return@addOnCompleteListener

                    val currentUserUid = firebaseAuth.currentUser!!.uid
                    val deviceToken = FirebaseMessaging.getInstance().token

                    ApiManager.CurrentUser.onPutDeviceTokenOnUser(
                        currentUserUid, deviceToken.toString(),
                        {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }, {
                            makeToast("Failed setting Device Token for user")
                        }
                    )
                }
                .addOnFailureListener {
                    makeToast("Failed to Authenticate: ${it.message}")
                }
        } else {
            isRequired(signInInputsArray)
        }
    }

    // Disable the menu on this activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }


}