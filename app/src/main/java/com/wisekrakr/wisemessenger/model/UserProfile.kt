package com.wisekrakr.wisemessenger.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class UserProfile(
    var uid: String,
    var username: String,
    var status: String = "",
    var avatarUrl: String = "",
    var bannerUrl: String = "",
) : Serializable {

    var createdAt: Date = Date()
    var updatedAt: Date? = null

    var location: Location? = null
    var openChatRooms: MutableList<ChatRoom> = mutableListOf()
    var contacts: MutableList<String> = mutableListOf()

    constructor() : this("", "", "")


}