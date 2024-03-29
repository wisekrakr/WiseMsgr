package com.wisekrakr.wisemessenger.components.activity.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.databinding.ActivityProfileSettingsBinding
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch
import java.util.*

class ProfileSettingsActivity : BaseActivity<ActivityProfileSettingsBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityProfileSettingsBinding =
        ActivityProfileSettingsBinding::inflate

    private var profile: UserProfile? = null
    private var selectedAvatar: Uri? = null
    private var selectedBanner: Uri? = null
    private var profileMap: HashMap<String, String> = hashMapOf()

    override fun setup() {

        getUserProfile()

        val username = intent.getStringExtra("username")

        binding.txtUsernameSettings.setText(username)

        binding.btnSaveSettings.setOnClickListener {
            startUploadProcess()
        }

        // Select avatar clicked
        binding.imgBtnAvatarSettings.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            setAvatarContent.launch(intent)
        }

        // Select banner clicked
        binding.imgBannerSettings.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            setBannerContent.launch(intent)
        }
    }

    private fun getUserProfile() {
        if (currentUser!!.profileUid.isBlank()) {
            makeToast("Please fill out your profile.")
        } else {
            launch {
                ApiManager.Profiles.onGetUser(currentUser!!.uid) { userProfile ->
                    profile = userProfile

                    populateEditableItems(userProfile)
                }
            }
        }
    }


    private fun populateEditableItems(profile: UserProfile) {
        if (profile.username.isNotEmpty())
            binding.txtUsernameSettings.setText(profile.username)
        if (profile.status.isNotEmpty())
            binding.txtStatusSettings.setText(profile.status)
        if (profile.avatarUrl.isNotEmpty()) {
            binding.imgBtnAvatarSettings.alpha = 0f
            Actions.ImageActions.loadImage(profile.avatarUrl, binding.circleImageAvatarSettings)
        }
        if (profile.bannerUrl.isNotEmpty())
            Actions.ImageActions.loadImage(profile.bannerUrl, binding.imgBannerSettings)
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
    private fun onUpdateUserProfile(profileMap: HashMap<String, String>) {

        profileMap["uid"] = currentUser!!.uid
        profileMap["username"] = binding.txtUsernameSettings.text.toString()
        profileMap["status"] = binding.txtStatusSettings.text.toString()

//        if(profile != null){
//            profileMap["avatarUrl"] = profile!!.avatarUrl
//            profileMap["bannerUrl"] = profile!!.bannerUrl
//        }

        val userProfile = UserProfile(
            profileMap["uid"].toString(),
            profileMap["username"].toString(),
            profileMap["status"].toString(),
            profileMap["avatarUrl"].toString(),
            profileMap["bannerUrl"].toString(),
        )

        userProfile.updatedAt = Date()

        if (profile?.chatRooms != null) {
            userProfile.chatRooms = profile?.chatRooms!!
        }

        if (profile?.contacts != null) {
            userProfile.contacts = profile?.contacts!!
        }

        launch {
            ApiManager.Profiles.onSaveUserProfile(
                userProfile,
                {
                    makeToast("Saved profile successfully!")

                    ApiManager.CurrentUser.onUpdateUser(
                        profileMap,
                        {
                            makeToast("Updated user successfully!")
                        },
                        {
                            makeToast("Failed updating user")
                        }
                    )
                }, {
                    makeToast("Failed saving profile ")
                }
            )
        }

    }


    /**
     * If an avatar was selected, upload it to the Firebase Storage and update user
     * Else update user
     */
    private fun startUploadProcess() {
        launch {


            if (selectedAvatar != null) {
                ApiManager.Profiles.onSaveUserProfileImage(
                    selectedAvatar,
                    STORAGE_AVATARS,
                    profileMap
                ) {
                    onUpdateUserProfile(it)
                }
            }

            if (selectedBanner != null) {
                ApiManager.Profiles.onSaveUserProfileImage(
                    selectedBanner,
                    STORAGE_BANNERS,
                    profileMap
                ) {
                    onUpdateUserProfile(it)
                }
            }

            if (profile != null) {

                profile?.let {
                    profileMap["avatarUrl"] = it.avatarUrl
                    profileMap["bannerUrl"] = it.bannerUrl
                }
            }
            onUpdateUserProfile(profileMap)

        }
    }

    /**
     * Used to be startActivityForResult
     * setContent launches a new Intent of picking an image.
     * Returns a result, with the data of the image.
     * We use the image URI to set the ImageButton in the UI
     */
    private val setAvatarContent =
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
    private val setBannerContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data: Intent? = result.data
                selectedBanner = data!!.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedBanner)
                binding.imgBannerSettings.setImageBitmap(bitmap)
            }
        }

}