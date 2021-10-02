package com.wisekrakr.wisemessenger.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class UserProfile(var uid: String, var username: String, var status: String = "") : Serializable{

    var createdAt: Date = Date()
    var location: Location? = null
    var avatarUrl: String = ""
    var bannerUrl: String = ""
    var participatingChatRooms : MutableList<String> = mutableListOf()

    constructor() : this("","","")


}