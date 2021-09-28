package com.wisekrakr.wisemessenger.model

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Group(var groupName: String) : Serializable {

    var uid: String = ""
    var avatarUrl: String = ""
    var chatRoomUid: String = ""
    var createdAt: Date = Date()


    constructor() : this("")
}
