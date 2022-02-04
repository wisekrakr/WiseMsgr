package com.wisekrakr.wisemessenger.appservice.tasks

import com.wisekrakr.wisemessenger.api.model.nondata.RequestType

interface ChatRequestApi {

    /**
     * Remove request from Current User's and "other" User's Chat Request list
     */
    fun onDeleteChatRequest(uidOne: String, uidTwo: String,completeListener: () -> Unit,)


    fun onSaveChatRequest(
        userProfileUid: String,
        userProfileUsername: String,
        currentUserUid: String,
        currentUsername: String,
        requestType: RequestType,
        completeListener: () -> Unit,
    )
}