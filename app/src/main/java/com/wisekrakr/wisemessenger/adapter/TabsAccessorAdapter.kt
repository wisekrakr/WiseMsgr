package com.wisekrakr.wisemessenger.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wisekrakr.wisemessenger.app.fragments.ContactsFragment
import com.wisekrakr.wisemessenger.app.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.app.fragments.PrivateChatFragment

class TabsAccessorAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 ->{
                val bundle = Bundle()
                bundle.putString("ContactsFragment", "Contacts")
                val contactsFragment = ContactsFragment()
                contactsFragment.arguments = bundle
                return contactsFragment
            }
            1 ->{
                val bundle = Bundle()
                bundle.putString("PrivateChatFragment", "Groups")
                val privateChatFragment = PrivateChatFragment()
                privateChatFragment.arguments = bundle
                return privateChatFragment
            }
            2 ->{
                val bundle = Bundle()
                bundle.putString("GroupChatFragment", "Groups")
                val groupChatFragment = GroupsFragment()
                groupChatFragment.arguments = bundle
                return groupChatFragment
            }
            else -> {
                throw IllegalStateException("Could not create now fragment")
            }
        }
    }


}