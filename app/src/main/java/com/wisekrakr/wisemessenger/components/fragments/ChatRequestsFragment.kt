package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.wisekrakr.wisemessenger.api.adapter.ChatRequestsAdapter
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.FragmentChatRequestsBinding
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch

class ChatRequestsFragment : BaseFragment<FragmentChatRequestsBinding>() {

    override val bindingInflater: BindingInflater<FragmentChatRequestsBinding> =
        FragmentChatRequestsBinding::inflate

    private lateinit var chatRequestsAdapter: ChatRequestsAdapter
    private var requests: ArrayList<ChatRequest> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRequestsAdapter = ChatRequestsAdapter()

        showRequests()

        chatRequestsAdapter.setClickListener(onButtonClick)

    }

    private fun showRequests() {

        launch {
            ApiManager.Requests.onGetAllChatRequestForUser(
                HomeActivity.currentUser!!.uid,
                requests,
                this@ChatRequestsFragment
            ) {
                RecyclerViewDataSetup.requests(
                    chatRequestsAdapter,
                    it,
                    viewBinding.recyclerViewRequests,
                    requireContext()
                )

                viewBinding.tvNumberOfRequestsRequests.text = requests.size.toString()
            }
        }
    }

    private fun removeRequests(chatRequest: ChatRequest) {
        launch {
            ApiManager.Requests.onSaveChatRequest(
                chatRequest.to,
                chatRequest.toUsername,
                chatRequest.from,
                chatRequest.fromUserName,
                RequestType.CANCELLED, {
                    requests.remove(chatRequest)
                    chatRequestsAdapter.setData(requests)
                    viewBinding.tvNumberOfRequestsRequests.text = requests.size.toString()
                }, {
                    Log.d(FRAGMENT_TAG, "Could not remove chat request from database")
                }
            )
        }
    }


    private val onButtonClick = object : ChatRequestsAdapter.OnButtonClickListener {
        override fun onAcceptClicked(position: Int) {
            launch {
                ApiManager.Requests.onSaveChatRequest(
                    requests[position].to,
                    requests[position].toUsername,
                    requests[position].from,
                    requests[position].fromUserName,
                    RequestType.ACCEPT, {
                        makeToast("Successfully accepted request: added user to Contacts")

                        addUserToANewChatRoom(requests[position])

                        removeRequests(requests[position])
                    }, {
                        makeToast("Failed to save Chat Request")
                    }
                )
            }
        }

        override fun onIgnoreClicked(position: Int) {

            makeToast("Successfully cancelled request!")

            removeRequests(requests[position])
        }
    }

    private fun addUserToANewChatRoom(request: ChatRequest) {

        launch {

            val users = arrayListOf(
                Conversationalist(
                    request.to,
                    request.toUsername
                ),
                Conversationalist(
                    request.from,
                    request.fromUserName
                )
            )

            val chatRoom = ApiManager.Rooms.onCreateNewChatRoom(
                users,
                true
            )

            users.forEach {
                ApiManager.Profiles.onUpdateUserWithNewChatRoom(
                    chatRoom,
                    it.uid
                )
            }
        }
    }
}