package com.wisekrakr.wisemessenger.app.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import com.google.firebase.auth.FirebaseUser
import com.wisekrakr.wisemessenger.app.activity.auth.LoginActivity
import com.wisekrakr.wisemessenger.app.activity.auth.RegisterActivity
import com.wisekrakr.wisemessenger.databinding.ActivityStartBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast

class StartActivity : BaseActivity<ActivityStartBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityStartBinding
         = ActivityStartBinding::inflate


    override fun setup() {

        binding.btnRegisterStart.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLoginStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun supportBar() {
        actionBar?.hide()

    }

    /**
     * Is the current user still signed in
     */
    override fun onStart() {
        super.onStart()

        if(firebaseAuth != null){
            val user: FirebaseUser? = firebaseAuth.currentUser
            user?.let {
                startActivity(Intent(this, HomeActivity::class.java))
                makeToast("Welcome Back ${user.displayName}")
            }
        }

    }

    // Disable the menu on this activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}