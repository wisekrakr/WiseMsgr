package com.wisekrakr.wisemessenger.components.activity.chat

import android.content.Intent
import android.view.LayoutInflater
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.components.activity.profile.ProfileActivity
import com.wisekrakr.wisemessenger.databinding.ActivityContactsBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.repository.UserProfileRepository
import kotlinx.coroutines.launch

class ContactsActivity : BaseActivity<ActivityContactsBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityContactsBinding =
        ActivityContactsBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var arrayContacts = ArrayList<UserProfile>()


    override fun setup() {

        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)

    }

    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: UserProfile) {
            showProfile(contact)
        }
    }

    private fun showProfile(userProfile: UserProfile) {
        val intent = Intent(this, ProfileActivity::class.java)
            .putExtra(SearchActivity.USER_PROFILE_KEY, userProfile)
        startActivity(intent)
        finish()
    }

    private fun onShowContacts() {
        launch {
            EventManager.onGetAllContactsOfCurrentUser {
                getContact(it)
            }
        }
    }

    private fun getContact(uid: String) {
        UserProfileRepository.getUserProfile(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserProfile::class.java)

                    if (userProfile?.uid != FirebaseUtils.firebaseAuth.uid) {
                        arrayContacts.add(userProfile!!)
                    }

                    RecyclerViewDataSetup
                        .contacts(
                            contactsAdapter,
                            arrayContacts,
                            binding.recyclerViewContacts,
                            this@ContactsActivity
                        )
                    binding.tvNumberOfContactsContacts.text = arrayContacts.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun supportBar() {
        supportActionBar?.title = "My Contacts"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}