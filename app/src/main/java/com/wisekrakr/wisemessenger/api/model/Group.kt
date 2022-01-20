package com.wisekrakr.wisemessenger.api.model

import java.io.Serializable
import java.util.*

data class Group(var groupName: String) : Serializable {

    var uid: String = ""
    var avatarUrl: String = ""
    var chatRoomUid: String = ""
    var createdAt: Date = Date()


    constructor() : this("")
}
