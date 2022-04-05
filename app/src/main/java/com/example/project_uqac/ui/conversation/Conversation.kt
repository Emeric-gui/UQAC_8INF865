package com.example.project_uqac.ui.conversation

class Conversation {
    var titleObject: String? = null
    var user1: String?= null
    var user2: String? = null
    var lastMessage: String? = null
    var chat: String? = null
    var timestamp: Double? = null

    constructor(titleObject: String?, user1: String?, user2: String?, lastMessage: String?, chat: String?, timestamp: Double?  )
    {
        this.titleObject=titleObject
        this.chat=chat
        this.lastMessage=lastMessage
        this.user1=user1
        this.user2=user2
        this.timestamp=timestamp
    }

    constructor() {}
}

