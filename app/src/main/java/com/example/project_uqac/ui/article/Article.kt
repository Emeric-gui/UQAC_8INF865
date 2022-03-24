package com.example.project_uqac.ui.article

class Article(
    val title: String = "", val marque: String = "", val date: Int = 0,
    val description: String = "", val image: String = "", val lat: String? = "", val lon: String? = "") {
    companion object {
        private var objectId = 0
        fun createContactsList(numObject: Int) : ArrayList<Article> {
            val articles = ArrayList<Article>()
            for (i in 1..numObject) {
                ++objectId
                articles.add(
                    Article("Objet $objectId", "Marque $objectId", 0,
                    "Description $objectId", "https://picsum.photos/600/300?random&$i", null, null
                )
                )
            }
            return articles
        }
    }
}

