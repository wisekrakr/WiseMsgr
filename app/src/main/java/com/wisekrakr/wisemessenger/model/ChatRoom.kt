package com.wisekrakr.wisemessenger.model

import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import java.io.Serializable
import kotlin.collections.ArrayList

data class ChatRoom(
    var participants: MutableList<Conversationalist>,
    var isPrivate: Boolean
) :
    Serializable {
    var uid: String = ""
    var messages: HashMap<String,String> = hashMapOf()

    constructor() : this(mutableListOf(), false)
}

