package com.wisekrakr.wisemessenger.api.model.nondata

import java.io.Serializable

data class Conversationalist(var uid: String, var username: String) : Serializable {

    constructor() : this("", "")

    override fun equals(other: Any?): Boolean {

        other as Conversationalist

        if(this === other)return true
        return this.uid === other.uid
    }
}