package com.wisekrakr.wisemessenger.model

import com.wisekrakr.wisemessenger.model.nondata.RequestType
import java.io.Serializable
import java.util.*

data class ChatRequest(val to: String,val toUsername: String, val from: String, val fromUserName: String,  val requestType: RequestType) : Serializable {

    var createdAt = Date()

    constructor() : this("","","","", RequestType.NONE)
}

