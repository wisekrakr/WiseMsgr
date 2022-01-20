package com.wisekrakr.wisemessenger.api.model

import android.location.Location
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class UserProfile(
    var uid: String,
    var username: String,
    var status: String = "",
    var avatarUrl: String = "",
    var bannerUrl: String = "",
    var chatRooms: HashMap<String, Boolean> = hashMapOf(),
) : Serializable {

    val createdAt: Date = Date()
    var updatedAt: Date? = null

    var location: Location? =  null//Location(LocationManager.GPS_PROVIDER)


    constructor() : this("", "", "")


}