package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.util.ArrayList

interface GroupApi {

    /**
     * Adds a Conversationalist/Contact to Group in the Firebase Database
     * @param conversationalist Conversationalist
     * @param group Group
     * @param chatRoom ChatRoom
     */
    fun onAddContactToGroup(conversationalist: Conversationalist, group: Group, chatRoom: ChatRoom)

    /**
     * Gets all Groups of a user
     * @param userUid String user uid
     * @param groups ArrayList of groups to show
     * @param setupViewBinding Unit handles setting up the view binding with a Group list
     */
    fun onGetAllGroupsOfCurrentUser(
        userUid: String,
        groups: ArrayList<Group>,
        setupViewBinding: (ArrayList<Group>) -> Unit,
    )

    /**
     * Gets all Groups of a user
     * @param userUid String user uid
     * @param groupUid String group uid
     * @param continuation Unit handles any other tasks to do
     */
    fun onGetAllGroupsOfCurrentUser(
        userUid: String,
        groupUid: String,
        continuation: (Group)->Unit
    )

    /**
     * Saves a group to the Firebase Database
     * @param userUid String user uid
     * @param group Group
     * @param chatRoom ChatRoom
     * @param context Context of the current Activity or Fragment this method is used in
     */
    fun onSaveGroup(userUid:String, group: Group, chatRoom: ChatRoom,context: Context)
}