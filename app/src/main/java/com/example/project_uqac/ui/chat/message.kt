package com.example.project_uqac.ui.chat

import java.util.*

class Message {
    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0
    var photoUrl: String? = null
    var imageUrl: String? = null

    constructor(messageText: String?, messageUser: String?, photoUrl: String?, imageUrl: String?) {
        this.messageText = messageText
        this.messageUser = messageUser
        this.photoUrl = photoUrl
        this.imageUrl = imageUrl

        // Initialize to current time
        messageTime = Date().time
    }

    constructor() {}
}