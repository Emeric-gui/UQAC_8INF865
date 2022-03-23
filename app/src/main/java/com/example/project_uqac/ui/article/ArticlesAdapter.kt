package com.example.project_uqac.ui.article

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.squareup.picasso.Picasso


class ArticlesAdapter (private val mArticles: List<Article>) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>()
{
    private lateinit var mListener: onItemClickListener

    private lateinit var mObjet: ArrayList<String>
    private lateinit var mLieu: ArrayList<String>
    private lateinit var mDate: ArrayList<String>

    init {
        mObjet = ArrayList<String>()
        mLieu = ArrayList<String>()
        mDate = ArrayList<String>()
    }


    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.itemTitreObjectArticle)
        val lieuTextView: TextView = itemView.findViewById<TextView>(R.id.itemLastMessageArticle)
        val dateTextView: TextView = itemView.findViewById<TextView>(R.id.itemNameArticle)
        val descriptionTextView: TextView = itemView.findViewById<TextView>(R.id.itemDescriptionArticle)
        val imageImageView: ImageView = itemView.findViewById<ImageView>(R.id.imageViewArticle)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val articleView = inflater.inflate(R.layout.item_article, parent, false)
        // Return a new holder instance
        return ViewHolder(articleView, mListener)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val article: Article = mArticles[position]
        // Set item views based on your views and data model
        val titleView = viewHolder.titleTextView
        titleView.text = article.title
        val objet = titleView.text
        mObjet.add(objet.toString())

        val lieuView = viewHolder.lieuTextView
        lieuView.text = article.lieu
        val lieu = lieuView.text
        mLieu.add(lieu.toString())

        val dateView = viewHolder.dateTextView
        dateView.text = article.date
        val date = dateView.text
        mDate.add(date.toString())

        val descriptionView = viewHolder.descriptionTextView
        descriptionView.text = article.description
        Picasso.get().load(article.image).into(viewHolder.imageImageView)

    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mArticles.size
    }

    fun getObjet(position: Int): String{
        return mObjet.get(position)
    }

    fun getLieu(position: Int): String{
        return mLieu.get(position)
    }

    fun getDate(position: Int): String{
        return mDate.get(position)
    }
}
