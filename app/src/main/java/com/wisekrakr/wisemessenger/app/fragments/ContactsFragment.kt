package com.wisekrakr.wisemessenger.app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.wisekrakr.wisemessenger.app.activity.chat.PrivateChatActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.app.EventManager
import com.wisekrakr.wisemessenger.app.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.app.activity.profile.ProfileActivity
import com.wisekrakr.wisemessenger.databinding.FragmentContactsBinding
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class ContactsFragment : BaseFragment<FragmentContactsBinding>() {

    override val bindingInflater: BindingInflater<FragmentContactsBinding> =
        FragmentContactsBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var arrayContacts = ArrayList<User>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)
    }


    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: User) {
            Log.d(FRAGMENT_TAG, "Clicked on: ${contact.username} ")

        }
    }


    private fun onShowContacts() {
        launch {
            EventManager.getAllUsers(arrayContacts) {
                RecyclerViewDataSetup
                    .contacts(
                        contactsAdapter,
                        arrayContacts,
                        viewBinding.recyclerViewContacts,
                        requireContext()
                    )

                viewBinding.tvNumberOfContactsContacts.text = arrayContacts.size.toString()
            }
        }
    }
}