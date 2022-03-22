package com.example.project_uqac.ui.my_account.myPosts

class Post (val title: String, val lieu: String, val date: String, val description: String, val image: String) {
    companion object {
        private var objectId = 0
        fun createContactsList(numObject: Int) : ArrayList<Post> {
            val articles = ArrayList<Post>()
            for (i in 1..numObject) {
                ++objectId
                articles.add(
                    Post("Objet $objectId", "Lieu $objectId", "Date $objectId",
                    "Description $objectId", "https://picsum.photos/600/300?random&$i"
                )
                )
            }
            return articles
        }
    }
}

