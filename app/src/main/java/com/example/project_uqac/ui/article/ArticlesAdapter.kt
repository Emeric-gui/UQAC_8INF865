package com.example.project_uqac.ui.article

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.squareup.picasso.Picasso
import android.location.Geocoder

class ArticlesAdapter (private val mArticles: List<Article>) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>()
{
    private lateinit var mListener: OnItemClickListener


    private var mObjet: ArrayList<String> = ArrayList()
    private var mLieu: ArrayList<String> = ArrayList()
    private var mDate: ArrayList<String> = ArrayList()
    private var mNom : ArrayList<String> = ArrayList()
    private var mMail : ArrayList<String> = ArrayList()




    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.itemTitreObjectArticle)
        val lieuTextView: TextView = itemView.findViewById<TextView>(R.id.itemLastMessageArticle)
        val dateTextView: TextView = itemView.findViewById<TextView>(R.id.itemNameArticle)
        val descriptionTextView: TextView = itemView.findViewById<TextView>(R.id.itemDescriptionArticle)
        val imageImageView: ImageView = itemView.findViewById<ImageView>(R.id.imageViewArticle)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(layoutPosition)

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


        //find city ofr lat and long
        val latitude = article.lat
        val longitude = article.lon
        val geoCoder = Geocoder(viewHolder.itemView.context)
        val adresse = geoCoder.getFromLocation(latitude, longitude, 1)[0]

        val texteLieu : String = adresse.locality+ ", "+adresse.adminArea+", "+adresse.countryName

        val locationView = viewHolder.lieuTextView
        locationView.text = adresse.locality
        mLieu.add(texteLieu)


        //changement dans la date
        val date_to_string = article.date.toString()
        val annee = date_to_string[0]+""+date_to_string[1]+date_to_string[2]+""+date_to_string[3]
        val mois = date_to_string[4]+""+date_to_string[5]
        val jour = date_to_string[6]+""+date_to_string[7]

        val date_string = "$jour-$mois-$annee"

        val dateView = viewHolder.dateTextView
        dateView.text = date_string
        mDate.add(date_string)


        //We don't display the name of the person
        //but we save it
        val person = article.nom
        mNom.add(person)

        //recupérer le mail sans l'afficher
        val mail = article.author
        mMail.add(mail)

        val descriptionView = viewHolder.descriptionTextView
        descriptionView.text = article.description
        Picasso.get().load(article.image).into(viewHolder.imageImageView)

    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mArticles.size
    }

    fun getObjet(position: Int): String{
        return mObjet[position]
    }

    fun getLieu(position: Int): String{
        return mLieu[position]
    }

    fun getDate(position: Int): String{
        return mDate[position]
    }
    fun getNom(position: Int): String{
        return mNom[position]
    }
    fun getMail(position: Int): String{
        return mMail[position]
    }
}
