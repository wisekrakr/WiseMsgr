package com.wisekrakr.wisemessenger.api.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wisekrakr.wisemessenger.components.fragments.ChatRequestsFragment
import com.wisekrakr.wisemessenger.components.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.components.fragments.PrivateChatFragment

class TabsAccessorAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 ->{
                val bundle = Bundle()
                bundle.putString("PrivateChatFragment", "Chat")
                val privateChatFragment = PrivateChatFragment()
                privateChatFragment.arguments = bundle
                return privateChatFragment
            }
            1 ->{
                val bundle = Bundle()
                bundle.putString("GroupChatFragment", "Groups")
                val groupChatFragment = GroupsFragment()
                groupChatFragment.arguments = bundle
                return groupChatFragment
            }
            2 ->{
                val bundle = Bundle()
                bundle.putString("ChatRequestsFragment", "Requests")
                val chatRequestsFragment = ChatRequestsFragment()
                chatRequestsFragment.arguments = bundle
                return chatRequestsFragment
            }
            else -> {
                throw IllegalStateException("Could not create now fragment")
            }
        }
    }
}