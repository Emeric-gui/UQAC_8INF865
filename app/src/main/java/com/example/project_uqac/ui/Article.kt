package com.example.project_uqac.ui

class Article (val title: String, val lieu: String, val date: String,val description: String) {
    companion object {
        private var objectId = 0
        fun createContactsList(numObject: Int) : ArrayList<Article> {
            val articles = ArrayList<Article>()
            for (i in 1..numObject) {
                ++objectId
                articles.add(Article("Objet $objectId", "Lieu $objectId", "Date $objectId",
                    "Description $objectId"
                ))
            }
            return articles
        }
    }
}

