package com.wisekrakr.wisemessenger.app.activity.actions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.wisekrakr.wisemessenger.app.activity.BaseActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.app.EventManager
import com.wisekrakr.wisemessenger.app.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.app.activity.profile.ProfileActivity
import com.wisekrakr.wisemessenger.app.fragments.ContactsFragment
import com.wisekrakr.wisemessenger.databinding.ActivitySearchBinding
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivitySearchBinding = ActivitySearchBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts: ArrayList<User> = arrayListOf()


    override fun setup() {

        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)

    }

    companion object {
        const val CONTACT_KEY = "contact"
    }

    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: User) {
            Log.d(ACTIVITY_TAG, "Clicked on: ${contact.username} ")

            showProfile(contact)
        }
    }

    private fun showProfile(contact: User) {
        val intent = Intent(this, ProfileActivity::class.java)
            .putExtra(CONTACT_KEY, contact)
        startActivity(intent)
        finish()
    }

    private fun onShowContacts() {
        launch {
            EventManager.getAllUsers(contacts) {
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