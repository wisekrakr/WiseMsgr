package com.wisekrakr.wisemessenger.app

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.adapter.GroupsAdapter
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.repository.UserRepository
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

object EventManager {

    fun getAllUsers(list: ArrayList<User>, setupViewBinding: (ArrayList<User>) -> Unit) {

        UserRepository.getUsers()
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
//                            Log.d(FRAGMENT_TAG, it.toString())
                        val user = it.getValue(User::class.java)!!

                        if (user.uid != FirebaseUtils.firebaseAuth.uid) {
                            list.add(it.getValue(User::class.java)!!)
                        }
                    }

                    setupViewBinding(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
    }

    fun getAllGroupsOfCurrentUser(
        currentUser: User,
        groups: ArrayList<Group>,
        groupsAdapter: GroupsAdapter,
        recyclerView: RecyclerView,
        context: Context
        , setupViewBinding: (ArrayList<Group>) -> Unit
    ) {
        getGroupsUser(currentUser.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val group = it.getValue(Group::class.java)!!

                    if (group.groupName.isNotEmpty()) {
                        Log.d(TAG, "Group added to array ${group.groupName}")
                        groups.add(group)
                        groupsAdapter.notifyDataSetChanged()

                    }
                }

                groupsAdapter.setData(groups)


                recyclerView.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = groupsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)

            }

        })
    }
}