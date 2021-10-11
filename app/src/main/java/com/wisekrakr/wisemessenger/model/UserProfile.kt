package com.wisekrakr.wisemessenger.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    var chatRooms: HashMap<String, Boolean> = hashMapOf()
    var contacts: MutableList<String> = mutableListOf()

    constructor() : this("", "", "")


}