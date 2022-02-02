package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.adapter.ChatRequestsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.FragmentChatRequestsBinding
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.api.repository.ChatRequestRepository.getChatRequestsForCurrentUser
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
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
            getChatRequestsForCurrentUser(
                HomeActivity.currentUser!!.uid
            ).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {
                        val request = it.getValue(ChatRequest::class.java)

                        if (request != null) {
                            Log.d(FRAGMENT_TAG, request.toString())

                            if (request.requestType == RequestType.RECEIVED)
                                requests.add(request)
                            if (request.requestType == RequestType.SENT)
                                requests.add(request)
                        }
                    }

                    if(isAdded){
                        RecyclerViewDataSetup.requests(
                            chatRequestsAdapter,
                            requests,
                            viewBinding.recyclerViewRequests,
                            requireContext()
                        )

                        viewBinding.tvNumberOfRequestsRequests.text = requests.size.toString()
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(FRAGMENT_TAG, error.message)

                }
            })


        }
    }

    private fun removeRequests(chatRequest: ChatRequest) {
        launch {
            EventManager.onSaveChatRequest(
                chatRequest.to,
                chatRequest.toUsername,
                chatRequest.from,
                chatRequest.fromUserName,
                RequestType.CANCELLED
            ) {
                requests.remove(chatRequest)
                chatRequestsAdapter.setData(requests)
                viewBinding.tvNumberOfRequestsRequests.text = requests.size.toString()
            }
        }
    }


    private val onButtonClick = object : ChatRequestsAdapter.OnButtonClickListener {
        override fun onAcceptClicked(position: Int) {
            launch {
                EventManager.onSaveChatRequest(
                    requests[position].to,
                    requests[position].toUsername,
                    requests[position].from,
                    requests[position].fromUserName,
                    RequestType.ACCEPT
                ) {
                    makeToast("Successfully accepted request: added user to Contacts")

                    addUserToANewChatRoom(requests[position])


                    removeRequests(requests[position])
                }
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

            val chatRoom = EventManager.onCreateNewChatRoom(
                users,
                true
            )

            users.forEach {
                UserProfileRepository.updateUserWithANewChatRoom(
                    chatRoom,
                    it.uid
                )
            }
        }
    }
}