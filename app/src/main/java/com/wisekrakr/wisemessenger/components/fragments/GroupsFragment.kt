package com.wisekrakr.wisemessenger.components.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.adapter.GroupsAdapter
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.api.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.appservice.tasks.TaskManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.components.activity.actions.CreateGroupActivity
import com.wisekrakr.wisemessenger.components.activity.chat.GroupChatActivity
import com.wisekrakr.wisemessenger.databinding.FragmentGroupsBinding
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class GroupsFragment : BaseFragment<FragmentGroupsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGroupsBinding =
        FragmentGroupsBinding::inflate

    private lateinit var groupsAdapter: GroupsAdapter

    private lateinit var chatRoom: ChatRoom
    private var arrayGroups = ArrayList<Group>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupsAdapter = GroupsAdapter()

        viewBinding.btnAddGroupGroups.setOnClickListener {
            startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
            activity?.finish()
        }

        onShowGroups()

        groupsAdapter.setClickListener(onGroupClick)
    }

    companion object {
        const val GROUP_KEY = "group"
        const val CHAT_ROOM_KEY = "chat_room"
    }

    private fun onShowGroups() {
        launch {

            TaskManager.Groups.onGetAllGroupsOfCurrentUser(
                currentUser!!.uid,
                arrayGroups
            ) {
                if (isAdded) {
                    RecyclerViewDataSetup
                        .groups(
                            groupsAdapter,
                            it,
                            viewBinding.recyclerViewGroups,
                            requireContext()
                        )

                    viewBinding.tvNumberOfContactsGroups.text = arrayGroups.size.toString()
                }

            }
        }
    }

    private val onGroupClick = object : GroupsAdapter.OnItemClickListener {
        override fun onClick(group: Group) {
            Log.d(FRAGMENT_TAG, "Clicked on group: ${group.groupName} ")

            launch {
                getGroupsUser(currentUser?.uid.toString()).child(group.uid)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val g = snapshot.getValue(Group::class.java)


                            if (g?.uid == group.uid) {
                                startChatting(g)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(FRAGMENT_TAG, error.message)
                        }
                    })

            }
        }
    }

    private fun startChatting(group: Group) {
        launch {
            getChatRoom(group.chatRoomUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatRoom = snapshot.getValue(ChatRoom::class.java)!!
                        val intent = Intent(requireContext(), GroupChatActivity::class.java)
                            .putExtra(GROUP_KEY, group)
                            .putExtra(CHAT_ROOM_KEY, chatRoom)
                        startActivity(intent)
                        activity?.finish()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }


    }
}