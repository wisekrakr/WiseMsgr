package com.wisekrakr.wisemessenger.api.model

import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import java.io.Serializable
import java.util.*

data class Notification(
    val to: String,
    val toUsername: String,
    val from: String,
    val fromUserName: String,
    val message: String,
    val type: NotificationType,
) :
    Serializable {

    var createdAt = Date()

    constructor() : this("", "", "", "", "", NotificationType.NONE)

}
