package com.wisekrakr.wisemessenger.model

import java.io.Serializable
import java.util.*

data class User(var uid: String, var username: String, var email: String) : Serializable{

    var createdAt: Date = Date()
    var profileUid: String = ""

    constructor() : this("","","")


}