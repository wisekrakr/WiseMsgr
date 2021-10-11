package com.wisekrakr.wisemessenger.components.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.EventManager.onGetAllContactsOfCurrentUser
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.components.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.components.activity.profile.ProfileActivity
import com.wisekrakr.wisemessenger.databinding.FragmentContactsBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.repository.UserProfileRepository.getUserProfileChatRooms
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
            showProfile(contact)
        }
    }

    private fun showProfile(userProfile: UserProfile) {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
            .putExtra(SearchActivity.USER_PROFILE_KEY, userProfile)
        startActivity(intent)
        activity?.finish()
    }

    private fun onShowContacts() {
        launch {
            onGetAllContactsOfCurrentUser{
                getContact(it)
            }
        }
    }

    private fun getContact(uid: String) {
        getUserProfile(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserProfile::class.java)

                    if (userProfile?.uid != firebaseAuth.uid) {
                        arrayContacts.add(userProfile!!)
                    }

                    RecyclerViewDataSetup
                        .contacts(
                            contactsAdapter,
                            arrayContacts,
                            viewBinding.recyclerViewContacts,
                            requireContext()
                        )
                    viewBinding.tvNumberOfContactsContacts.text = arrayContacts.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}