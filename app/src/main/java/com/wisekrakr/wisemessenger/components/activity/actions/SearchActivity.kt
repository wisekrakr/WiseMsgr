package com.wisekrakr.wisemessenger.components.activity.actions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.profile.ProfileActivity
import com.wisekrakr.wisemessenger.databinding.ActivitySearchBinding
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivitySearchBinding = ActivitySearchBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts: ArrayList<UserProfile> = arrayListOf()


    override fun setup() {

        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)
    }

    companion object {
        const val USER_PROFILE_KEY = "userProfile"
    }

    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: UserProfile) {
            Log.d(ACTIVITY_TAG, "Clicked on: ${contact.username} ")

            showProfile(contact)
        }
    }


    private fun showProfile(userProfile: UserProfile) {
        val intent = Intent(this, ProfileActivity::class.java)
            .putExtra(USER_PROFILE_KEY, userProfile)
        startActivity(intent)
        finish()
    }

    private fun onShowContacts() {
        launch {
            EventManager.onGetAllUsers(contacts) {
                RecyclerViewDataSetup.contacts(
                    contactsAdapter,
                    contacts,
                    binding.recyclerviewSearchFriend,
                    this@SearchActivity
                )
            }
        }
    }

    override fun supportBar() {
        supportActionBar?.title = "Search for a friend"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}