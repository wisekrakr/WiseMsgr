package com.wisekrakr.wisemessenger.fragments

import android.os.Bundle
import android.view.View
import com.wisekrakr.wisemessenger.databinding.FragmentPrivateChatBinding


class PrivateChatFragment : BaseFragment<FragmentPrivateChatBinding>() {

    override val bindingInflater: BindingInflater<FragmentPrivateChatBinding> =
        FragmentPrivateChatBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {

        }
    }

}