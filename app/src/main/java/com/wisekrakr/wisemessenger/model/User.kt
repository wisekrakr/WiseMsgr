package com.wisekrakr.wisemessenger.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class User(var uid: String, var username: String, var email: String) : Serializable{

    var createdAt: Date = Date()
    var status: String = ""
    var location: Location? = null
    var avatarUrl: String = ""
    var participatingChatRooms : MutableList<String> = mutableListOf()

    constructor() : this("","","")


}