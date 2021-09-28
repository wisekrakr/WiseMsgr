package com.wisekrakr.wisemessenger.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import com.wisekrakr.wisemessenger.activity.BaseActivity
import com.wisekrakr.wisemessenger.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.ActivityLoginBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.isRequired
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    private lateinit var signInInputsArray: Array<EditText>

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        = ActivityLoginBinding::inflate

    override fun setup() {
        signInInputsArray = arrayOf(binding.emailEditTextLogin, binding.passwordEditTextLogin)

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvNoAccountLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
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