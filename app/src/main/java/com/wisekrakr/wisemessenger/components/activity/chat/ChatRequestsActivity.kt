package com.wisekrakr.wisemessenger.components.activity.chat

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.adapter.ChatRequestsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.databinding.ActivityChatRequestsBinding
import com.wisekrakr.wisemessenger.model.ChatRequest
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.model.nondata.RequestType
import com.wisekrakr.wisemessenger.repository.ChatRequestRepository.getChatRequestsForCurrentUser
import com.wisekrakr.wisemessenger.repository.UserProfileRepository.updateUserWithANewChatRoom
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch

class ChatRequestsActivity : BaseActivity<ActivityChatRequestsBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityChatRequestsBinding =
        ActivityChatRequestsBinding::inflate

    private lateinit var chatRequestsAdapter: ChatRequestsAdapter
    private var requests: ArrayList<ChatRequest> = arrayListOf()


    override fun setup() {

        chatRequestsAdapter = ChatRequestsAdapter()

        showRequests()

        chatRequestsAdapter.setClickListener(onButtonClick)

    }

    private fun showRequests() {

        launch {
            getChatRequestsForCurrentUser(
                currentUser!!.uid
            ).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {
                        val request = it.getValue(ChatRequest::class.java)

                        if (request != null) {
                            Log.d(ACTIVITY_TAG, request.toString())

                            if (request.requestType == RequestType.RECEIVED)
                                requests.add(request)
                        }
                    }

                    chatRequestsAdapter.setData(requests)

                    binding.recyclerViewRequests.layoutManager = LinearLayoutManager(
                        this@ChatRequestsActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    binding.recyclerViewRequests.setHasFixedSize(true)
                    binding.recyclerViewRequests.adapter = chatRequestsAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ACTIVITY_TAG, error.message)

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
                updateUserWithANewChatRoom(
                    chatRoom,
                    it.uid
                )
            }

        }
    }

    override fun supportBar() {
        supportActionBar?.title = "Friend Requests"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}