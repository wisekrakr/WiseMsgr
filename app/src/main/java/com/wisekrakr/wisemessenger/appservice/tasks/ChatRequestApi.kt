package com.wisekrakr.wisemessenger.appservice.tasks

import androidx.fragment.app.Fragment
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import java.util.ArrayList

interface ChatRequestApi {

    /**
     * Remove request from Current User's and "other" User's Chat Request list
     * @param uidOne contact uid/ user uid
     * @param uidTwo contact uid/ user uid
     * @param completeListener handles completion state
     * @param failureListener handles failure state
     */
    fun onDeleteChatRequest(
        uidOne: String,
        uidTwo: String,
        completeListener: () -> Unit,
        failureListener: () -> Unit,
    )


    /**
     * Sends ChatRequest data to the Firebase Database. Saves it at chat-request.
     * @param userProfileUid uid of contact's user profile
     * @param userProfileUsername contact username in user profile data
     * @param currentUserUid uid of current user
     * @param currentUsername user name of current user
     * @param requestType type of request - SENT, RECEIVED, CANCELLED, ACCEPT, NONE
     * @param completeListener handles completion state
     * @param failureListener handles failure state
     */
    fun onSaveChatRequest(
        userProfileUid: String,
        userProfileUsername: String,
        currentUserUid: String,
        currentUsername: String,
        requestType: RequestType,
        completeListener: () -> Unit,
        failureListener: () -> Unit,
    )

    /**
     * Get all chat requests for user
     * @param userUid String uid for user
     * @param userProfileUid String uid of user profile
     * @param toggleButtons Unit handles toggling on and off of buttons
     */
    fun onGetAllChatRequestForUser(userUid: String, userProfileUid: String, toggleButtons: Unit)

    /**
     * Get all chat requests for user and setting of viewbinding
     * @param userUid String uid for user
     * @param requests ArrayList of chat requests to show
     * @param fragment Fragment to show the chat requests
     * @param setupViewBinding Unit handles setting up the viewbinding for the current fragment
     */
    fun onGetAllChatRequestForUser(
        userUid: String,
        requests: ArrayList<ChatRequest>,
        fragment: Fragment,
        setupViewBinding: (ArrayList<ChatRequest>) -> Unit,
    )
}