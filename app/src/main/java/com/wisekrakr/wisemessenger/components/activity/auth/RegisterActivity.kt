package com.wisekrakr.wisemessenger.components.activity.auth

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import com.google.firebase.messaging.FirebaseMessaging
import com.wisekrakr.wisemessenger.api.model.User
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.ActivityRegisterBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.updateFirebaseUser
import com.wisekrakr.wisemessenger.utils.Actions.IntentActions.returnToActivityWithFlags
import com.wisekrakr.wisemessenger.utils.Extensions
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch
import java.util.*

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {


    private lateinit var registerUsername: String
    private lateinit var registerEmail: String
    private lateinit var registerPassword: String
    private lateinit var registerInputsArray: Array<EditText>


    override val bindingInflater: (LayoutInflater) -> ActivityRegisterBinding =
        ActivityRegisterBinding::inflate

    override fun setup() {

        registerInputsArray = arrayOf(
            binding.usernameEditTextRegister,
            binding.emailEditTextRegister,
            binding.passwordEditTextRegister
        )

        // Register button clicked
        binding.btnRegister.setOnClickListener {
            register()
        }

        // Already have an account clicked
        binding.tvAlreadyRegisteredRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    override fun supportBar() {
        actionBar?.hide()
    }


    /**
     * Register function.
     * Checks if the text views are not empty and then creates/registers a new Firebase user
     */
    private fun register() {

        launch {
            if (!isNotEmpty(registerInputsArray)) {
                Extensions.isRequired(registerInputsArray)

            } else {
                Log.d(ACTIVITY_TAG, "Registering....")

                registerUsername = binding.usernameEditTextRegister.text.toString().trim()
                registerEmail = binding.emailEditTextRegister.text.toString().trim()
                registerPassword = binding.passwordEditTextRegister.text.toString().trim()

                firebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) return@addOnCompleteListener

                        if (task.isComplete) {
                            val currentUserUid = firebaseAuth.currentUser!!.uid
                            val deviceToken = FirebaseMessaging.getInstance().token

                            ApiManager.CurrentUser.onPutDeviceTokenOnUser(
                                currentUserUid,
                                deviceToken.toString(),
                                {
                                    saveUserToFirebaseDatabase()

                                    Log.d(ACTIVITY_TAG,
                                        "Successfully created user: ${task.result?.user?.uid}")

                                    makeToast("Created account successfully!")

                                    val intent =
                                        Intent(this@RegisterActivity, HomeActivity::class.java)
                                    intent.putExtra("username", registerUsername)

                                    startActivity(intent)
                                    finish()
                                }, {
                                    makeToast("Failed set Device Token for user")
                                }
                            )
                        }
                    }
                    .addOnFailureListener {
                        makeToast("Failed to Register: ${it.message}")
                    }
            }
        }
    }

    private fun saveUserToFirebaseDatabase() {
        Log.d(ACTIVITY_TAG, "saving user to firebase ${firebaseAuth.uid}")

        launch {
            ApiManager.CurrentUser.onSaveUser {
                it.ref.setValue(
                    User(
                        firebaseAuth.uid ?: "",
                        registerUsername,
                        registerEmail
                    )
                )
                    .addOnSuccessListener {
                        Log.d(ACTIVITY_TAG, "User saved to Firebase Database")

                        updateFirebaseUser(registerUsername)

                        returnToActivityWithFlags(HomeActivity::class.simpleName.toString())
                    }
                    .addOnFailureListener {
                        Log.d(ACTIVITY_TAG, "Failed saving user to database: ${it.cause}")
                    }
            }
        }
    }

    // Disable the menu on this activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }


}