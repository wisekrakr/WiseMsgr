package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.view.View
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.databinding.FragmentPrivateChatBinding
import com.wisekrakr.wisemessenger.model.ChatRoom


class PrivateChatFragment : BaseFragment<FragmentPrivateChatBinding>() {

    override val bindingInflater: BindingInflater<FragmentPrivateChatBinding> =
        FragmentPrivateChatBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var converstations : ArrayList<ChatRoom> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}