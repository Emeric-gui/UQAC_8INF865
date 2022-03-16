package com.example.project_uqac.ui.conversation

class Conversation(val titleObject: String, val name: String, val lastMessage: String, val image: String) {
    companion object {
        private var objectId = 0
        fun createConversationList(numObject: Int) : ArrayList<Conversation> {
            val conversations = ArrayList<Conversation>()
            for (i in 1..numObject) {
                ++objectId
                conversations.add(
                    Conversation("titleObjet $objectId", "name $objectId", "lastMessage $objectId", "https://picsum.photos/600/300?random&$i")
                )
            }
            return conversations
        }
    }
}

