package com.wisekrakr.wisemessenger.appservice.tasks

import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.util.ArrayList

interface GroupApi {
    fun onAddContactToGroup(conversationalist: Conversationalist, group: Group, chatRoom: ChatRoom)

    fun onGetAllGroupsOfCurrentUser(
        currentUserUid: String,
        groups: ArrayList<Group>,
        setupViewBinding: (ArrayList<Group>) -> Unit,
    )
}