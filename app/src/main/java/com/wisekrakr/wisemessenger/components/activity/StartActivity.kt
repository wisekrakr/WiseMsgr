package com.wisekrakr.wisemessenger.components.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import androidx.core.os.HandlerCompat
import com.google.firebase.auth.FirebaseUser
import com.wisekrakr.wisemessenger.components.activity.auth.LoginActivity
import com.wisekrakr.wisemessenger.components.activity.auth.RegisterActivity
import com.wisekrakr.wisemessenger.databinding.ActivityStartBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import java.util.logging.Handler

class StartActivity : BaseActivity<ActivityStartBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityStartBinding
         = ActivityStartBinding::inflate


    override fun setup() {

        if(firebaseAuth.uid == null){
            binding.btnRegisterStart.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            binding.btnLoginStart.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }else{
            binding.btnRegisterStart.visibility = View.INVISIBLE
            binding.btnLoginStart.visibility = View.INVISIBLE
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

        showAnimation()

        val handler = android.os.Handler()
        handler.postDelayed({
            if(firebaseAuth != null){
                val user: FirebaseUser? = firebaseAuth.currentUser
                user?.let {
                    startActivity(Intent(this, HomeActivity::class.java))
                    makeToast("Welcome Back ${user.displayName}")
                }
            }
        },3000)


    }

    // Disable the menu on this activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    private fun showAnimation(){
        val animatorLogo: ObjectAnimator = ObjectAnimator.ofFloat(
            binding.imageView2,"y",400f
        )
        val animatorName: ObjectAnimator = ObjectAnimator.ofFloat(
            binding.textView3,"x",830f
        )

        animatorLogo.duration = 2000
        animatorName.duration = 2000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorLogo, animatorName)
        animatorSet.start()
    }

}