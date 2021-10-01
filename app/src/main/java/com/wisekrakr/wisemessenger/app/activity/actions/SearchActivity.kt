package com.wisekrakr.wisemessenger.app.activity.actions

import android.view.LayoutInflater
import com.wisekrakr.wisemessenger.app.activity.BaseActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.app.EventManager
import com.wisekrakr.wisemessenger.app.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.databinding.ActivitySearchBinding
import com.wisekrakr.wisemessenger.model.User
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivitySearchBinding = ActivitySearchBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts: ArrayList<User> = arrayListOf()


    override fun setup() {

        contactsAdapter = ContactsAdapter()

        onShowContacts()
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