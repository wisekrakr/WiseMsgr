package com.wisekrakr.wisemessenger.app.activity.profile

import android.view.LayoutInflater
import com.wisekrakr.wisemessenger.app.activity.BaseActivity
import com.wisekrakr.wisemessenger.app.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.databinding.ActivityProfileBinding
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding = ActivityProfileBinding::inflate

    private lateinit var contact: User

    override fun setup() {
        contact = intent.getSerializableExtra(SearchActivity.CONTACT_KEY) as User

        showProfile()

        binding.btnSendRequestProfile.setOnClickListener {
            onSendRequest()
        }

        binding.btnCancelRequestProfile.setOnClickListener {
            onCancelRequest()
        }
    }

    private fun onSendRequest() {

    }


    private fun onCancelRequest() {

    }

    /**
     * Populate view components
     */
    private fun showProfile(){
        loadImage(contact.avatarUrl, binding.imgAvatarProfile)
        binding.tvUsernameProfile.text = contact.username
        binding.tvStatusProfile.text = contact.status

    }

    override fun supportBar() {
        supportActionBar?.title = contact.username
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}