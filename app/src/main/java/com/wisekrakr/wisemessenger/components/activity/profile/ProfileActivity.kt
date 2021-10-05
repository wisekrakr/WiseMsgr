package com.wisekrakr.wisemessenger.components.activity.profile

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.components.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.databinding.ActivityProfileBinding
import com.wisekrakr.wisemessenger.model.nondata.RequestType
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.repository.ChatRequestRepository.getChatRequestsForCurrentUser
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding =
        ActivityProfileBinding::inflate

    private lateinit var userProfile: UserProfile
    private lateinit var currentUserUid: String

    override fun setup() {
        userProfile = intent.getSerializableExtra(SearchActivity.USER_PROFILE_KEY) as UserProfile
        currentUserUid = currentUser!!.uid

        showProfile()

        if (currentUserUid != userProfile.uid) {
            binding.btnSendRequestProfile.setOnClickListener {
                onSendRequest(RequestType.SENT)
            }
        } else {
            binding.btnSendRequestProfile.visibility = INVISIBLE
        }


        binding.btnCancelRequestProfile.setOnClickListener {
            onSendRequest(RequestType.CANCELLED)
        }
    }

    private fun onSendRequest(requestType: RequestType) {

        launch {
            if (!userProfile.friends.contains(currentUserUid)) {
                EventManager.onSaveChatRequest(
                    userProfile.uid,
                    userProfile.username,
                    currentUserUid,
                    currentUser!!.username,
                    requestType
                ) {
                    if (requestType == RequestType.SENT) {
                        toggleButtons(false)

                        makeToast("Successfully requested chat!")
                    } else if (requestType == RequestType.CANCELLED) {
                        toggleButtons(true)

                        makeToast("Successfully cancelled request!")
                    }

                }
            }

        }
    }

    /**
     * Populate view components
     */
    private fun showProfile() {
        loadImage(userProfile.avatarUrl, binding.imgAvatarProfile)
        loadImage(userProfile.bannerUrl, binding.imgBannerProfile)
        binding.tvUsernameProfile.text = userProfile.username
        binding.tvStatusProfile.text = userProfile.status

        hasSentRequest()
    }

    private fun hasSentRequest() {
        launch {
            getChatRequestsForCurrentUser(currentUserUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(userProfile.uid)) {
                            toggleButtons(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    private fun toggleButtons(canceledRequest: Boolean) {
        if (canceledRequest) {
            binding.btnSendRequestProfile.visibility = VISIBLE
            binding.btnSendRequestProfile.isEnabled = true
            binding.btnCancelRequestProfile.visibility = INVISIBLE
            binding.btnCancelRequestProfile.isEnabled = false
        } else {
            binding.btnSendRequestProfile.visibility = INVISIBLE
            binding.btnSendRequestProfile.isEnabled = false
            binding.btnCancelRequestProfile.visibility = VISIBLE
            binding.btnCancelRequestProfile.isEnabled = true
        }

    }

    override fun supportBar() {
        supportActionBar?.title = userProfile.username
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}