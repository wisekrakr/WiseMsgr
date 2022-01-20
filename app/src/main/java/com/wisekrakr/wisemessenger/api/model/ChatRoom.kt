package com.wisekrakr.wisemessenger.api.model

import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.io.Serializable

data class ChatRoom(
    var participants: MutableList<Conversationalist>,
    var isPrivate: Boolean
) :
    Serializable {
    var uid: String = ""
    var messages: HashMap<String,String> = hashMapOf()

    constructor() : this(mutableListOf(), false)
}

