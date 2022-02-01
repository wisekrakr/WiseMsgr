package com.wisekrakr.wisemessenger.api.model

import java.io.Serializable

data class Status(var time: String, var date: String, var state: String) : Serializable{

    constructor() : this("","","")
}
