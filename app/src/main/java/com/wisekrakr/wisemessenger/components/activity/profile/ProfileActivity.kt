package com.wisekrakr.wisemessenger.components.activity.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.components.activity.contact.SearchActivity
import com.wisekrakr.wisemessenger.databinding.ActivityProfileBinding
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
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
        hasSentRequest()
        isAlreadyInContact()

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

    /**
     * This function removes the current chat room out of the both user profile's chat room list.
     * When deletion is successful, remove the chat room value from the chat room reference in the
     * database, deleting it in its entirety.
     */
    private fun onEndingConversation(chatRoom: ChatRoom) {
        launch {
            ApiManager.onEndingConversation(chatRoom, this@ProfileActivity) {
                toggleButtons(true)
            }
        }
    }

    /**
     * Creates a chat request in the database, which will be connected to this user profile,
     * So that this user profile can decide to accept or ignore it.
     * Gets all chat room participant of this user profile and calculates if the current user
     * is part of one of these chat rooms. If not, the current user can send an invite. Else we can
     * cancel the chat request.
     */
    private fun onSendRequest(requestType: RequestType) {
        launch {
            ApiManager.Profiles.onGetAllContactsOfCurrentUser {
                Log.d(ACTIVITY_TAG, "SENDING REQUEST $it")
                if (currentUserUid != it || it.isBlank()) {
                    ApiManager.Requests.onSaveChatRequest(
                        userProfile.uid,
                        userProfile.username,
                        currentUserUid,
                        currentUser!!.username,
                        requestType,{
                            if (requestType == RequestType.SENT) {
                                toggleButtons(false)

                                makeToast("Successfully requested chat!")
                            } else if (requestType == RequestType.CANCELLED) {
                                toggleButtons(true)

                                makeToast("Successfully cancelled request!")
                            }
                        },{
                            makeToast("Failed to sent request")
                        }
                    )
                }
            }
        }
    }

    /**
     * Populate view components
     * Shows the profile of the contact that was clicked
     */
    private fun showProfile() {
        loadImage(userProfile.avatarUrl, binding.imgAvatarProfile)
        loadImage(userProfile.bannerUrl, binding.imgBannerProfile)
        binding.tvUsernameProfile.text = userProfile.username
        binding.tvStatusProfile.text = userProfile.status
    }

    /**
     * Calculates if this current user has already send this profile a request to chat
     */
    private fun hasSentRequest() {
        launch {
            ApiManager.Requests.onGetAllChatRequestForUser(
                currentUserUid,
                userProfile.uid,
                toggleButtons(false)
            )
        }
    }

    /**
     * This user's profile has already accepted a request
     * Find chat rooms for this user profile and see if in any of these chat room has
     * the participant uid of the current user, if so, show "end conversation" button
     */
    private fun isAlreadyInContact() {
        launch {
            ApiManager.Profiles.onGetUserChatRooms(userProfile.uid) {
                ApiManager.Rooms.onGetChatRoom(
                    it,
                    userProfile.uid,
                    toggleButtons(false)
                ) {chatRoom->
                    binding.btnCancelRequestProfile.text = "End Conversation"

                    binding.btnCancelRequestProfile.setOnClickListener {
                        onEndingConversation(chatRoom)
                    }
                }
            }
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