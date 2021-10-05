package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.databinding.FragmentContactsBinding
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class ContactsFragment : BaseFragment<FragmentContactsBinding>() {

    override val bindingInflater: BindingInflater<FragmentContactsBinding> =
        FragmentContactsBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var arrayContacts = ArrayList<UserProfile>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)
    }


    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: UserProfile) {
            Log.d(FRAGMENT_TAG, "Clicked on: ${contact.username} ")
        }
    }

    private fun onShowContacts() {
        launch {
            EventManager.onGetAllUsers(arrayContacts) {
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