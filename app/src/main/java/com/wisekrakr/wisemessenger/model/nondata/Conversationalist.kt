package com.wisekrakr.wisemessenger.model.nondata

import java.io.Serializable

data class Conversationalist(var uid: String, var username: String) : Serializable {

    constructor() : this("", "")
}