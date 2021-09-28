package com.wisekrakr.wisemessenger.model

import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class ChatMessage(
    var sender: Conversationalist?,
    var recipients: MutableList<Conversationalist>,
    var message: String,
    var textColor: Int,
    var chatRoomUid: String?
) : Serializable {

    var uid: String = ""
    var messageType: Int = 0
    var date: Date = Date()

    constructor() : this(null, mutableListOf(),"",-1,"")

    companion object {
        const val TYPE_ME_MESSAGE = 0
        const val TYPE_OTHER_MESSAGE = 1
    }

}