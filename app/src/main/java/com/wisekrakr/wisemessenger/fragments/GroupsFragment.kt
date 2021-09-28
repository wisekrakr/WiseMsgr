package com.wisekrakr.wisemessenger.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.activity.HomeActivity.Companion.currentUser
import com.wisekrakr.wisemessenger.activity.actions.CreateGroupActivity
import com.wisekrakr.wisemessenger.activity.chat.GroupChatActivity
import com.wisekrakr.wisemessenger.adapter.GroupsAdapter
import com.wisekrakr.wisemessenger.databinding.FragmentGroupsBinding
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class GroupsFragment : BaseFragment<FragmentGroupsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGroupsBinding =
        FragmentGroupsBinding::inflate

    private lateinit var alertDialog: AlertDialog

    private lateinit var groupsAdapter: GroupsAdapter

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
    }

//    private fun onShowCreateGroupDialog() {
//
//        launch {
//            val builder = AlertDialog.Builder(requireContext())
//
//            alertDialog = builder.create()
//
//            alertDialog.setTitle("Enter Group Name: ")
//
//            val groupNameField = EditText(requireContext())
//            groupNameField.hint = "e.g. The Revengers"
//
//            // here we must create a list of users
//            // add them to arrayContacts when box is checked
//            dialogCreateGroupAdapter.setData(contactAdapter.getData())
//
//            alertDialog.setView(groupNameField)
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
//                "Create",
//                DialogInterface.OnClickListener { dialog, which ->
//                    val groupName = groupNameField.text.trim().toString()
//
//                    if (groupName.isEmpty()) return@OnClickListener
//
//                    onCreateGroup(groupName)
//
//                })
//
//            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, which ->
//                dialog.cancel()
//            }
//
//            builder.setAdapter(dialogCreateGroupAdapter) { dialog, which -> }
//
//            alertDialog.show()
//        }
//
//
//    }


    private fun onShowGroups() {
        launch {

            getGroupsUser(currentUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val group = it.getValue(Group::class.java)!!

                        if (group.groupName.isNotEmpty()) {
                            Log.d(FRAGMENT_TAG, "Group added to array ${group.groupName}")
                            arrayGroups.add(group)
                            groupsAdapter.notifyDataSetChanged()

                        }
                    }

                    groupsAdapter.setData(arrayGroups)

                    viewBinding.tvNumberOfContactsGroups.text = arrayGroups.size.toString()

                    viewBinding.recyclerViewGroups.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    viewBinding.recyclerViewGroups.setHasFixedSize(true)
                    viewBinding.recyclerViewGroups.adapter = groupsAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(FRAGMENT_TAG, error.message)

                }

            })
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
        val intent = Intent(requireContext(), GroupChatActivity::class.java)
            .putExtra(GROUP_KEY, group)
        startActivity(intent)
        activity?.finish()
    }
}