package com.wisekrakr.wisemessenger.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.wisekrakr.wisemessenger.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.databinding.ActivitySettingsBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.updateProfileUser
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivitySettingsBinding =
        ActivitySettingsBinding::inflate

    private var selectedAvatar: Uri? = null

    override fun setup() {

        populateEditableItems()

        binding.btnSaveSettings.setOnClickListener {
            uploadAvatarToFirebaseStorage()
        }

        // Select avatar clicked
        binding.imgBtnAvatarSettings.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }
    }

    private fun populateEditableItems(){
        if(currentUser == null) return

        binding.txtUsernameSettings.text = Editable.Factory.getInstance()
            .newEditable(currentUser?.username)

        if(currentUser!!.status.isNotEmpty()){
            binding.txtStatusSettings.text = Editable.Factory.getInstance()
                .newEditable(currentUser?.status)
        }

        if(currentUser!!.avatarUrl.isNotEmpty()){
//            Actions.ImageActions.loadImage(currentUser!!.avatarUrl, binding.circleImageAvatarSettings)

//
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(currentUser!!.avatarUrl.trim()))
//            binding.circleImageAvatarSettings.setImageBitmap(bitmap)
//            binding.imgBtnAvatarSettings.alpha = 0f
//
        }

    }


    override fun supportBar() {
        supportActionBar?.title = "Profile Settings"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Get profile data from activity binding, add it to a HashMap
     * Upload avatar (if one is selected) to firebase storage
     * Update FirebaseAuth currentuser's displayname and image
     * Set the new user value to the Database /users reference
     */
    private fun onUpdateUserProfile(profileMap : HashMap<String, String>) {
        val username = binding.txtUsernameSettings.text
        val status = binding.txtStatusSettings.text

        profileMap["uid"] = currentUser!!.uid
        profileMap["username"] = username.toString()
        profileMap["status"] = status.toString()

        updateProfileUser(this, profileMap)

    }


    /**
     * If an avatar was selected, upload it to the Firebase Storage and update user
     * Else update user
     */
    private fun uploadAvatarToFirebaseStorage() {
        launch {
            val profileMap = HashMap<String, String>()

            if (selectedAvatar != null) {
                val fileName = UUID.randomUUID().toString()
                val avatarRef = FirebaseUtils.firebaseStorage.getReference("/avatars/$fileName")

                avatarRef.putFile(selectedAvatar!!)
                    .addOnSuccessListener { it ->

                        Log.d(ACTIVITY_TAG, "Successfully uploaded image: ${it.metadata?.path}")

                        avatarRef.downloadUrl.addOnSuccessListener {
                            Log.d(ACTIVITY_TAG, "File location: $it")

                            profileMap["avatarUrl"] = it.toString()
                            onUpdateUserProfile(profileMap)
                        }
                    }
                    .addOnFailureListener {
                        Log.d(ACTIVITY_TAG, "Failed uploading image: ${it.cause}")
                        return@addOnFailureListener
                    }
            }else{
                onUpdateUserProfile(profileMap)
            }
        }
    }

    /**
     * Used to be startActivityForResult
     * getContent launches a new Intent of picking an image.
     * Returns a result, with the data of the image.
     * We use the image URI to set the ImageButton in the UI
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                val data: Intent? = result.data

                // handle the image/avatar
                selectedAvatar = data!!.data


                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedAvatar)

                binding.circleImageAvatarSettings.setImageBitmap(bitmap)
                binding.imgBtnAvatarSettings.alpha = 0f
            }
        }


}