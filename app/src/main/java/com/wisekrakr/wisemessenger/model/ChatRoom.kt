package com.wisekrakr.wisemessenger.model

import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import java.io.Serializable
import kotlin.collections.ArrayList

data class ChatRoom(
    var participants: MutableList<Conversationalist>,
) :
    Serializable {
    var uid: String = ""
    var messages: MutableList<ChatMessage> = mutableListOf()

    constructor() : this(mutableListOf())
}

