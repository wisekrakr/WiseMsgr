package com.wisekrakr.wisemessenger.api.model

import android.location.Location
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class UserProfile(
    var uid: String,
    var username: String,
    var status: String = "",
    var avatarUrl: String = "",
    var bannerUrl: String = "",
) : Serializable {

    val createdAt: Date = Date()
    var updatedAt: Date? = null
    var chatRooms: HashMap<String, Boolean> = hashMapOf()
    var contacts: HashMap<String, Conversationalist> = hashMapOf()

    var location: Location? =  null//Location(LocationManager.GPS_PROVIDER)
    var state : HashMap<String, String> = hashMapOf()


    constructor() : this("", "", "")

    override fun toString(): String {
        return "UserProfile(uid='$uid', username='$username', state=$state)"
    }


}