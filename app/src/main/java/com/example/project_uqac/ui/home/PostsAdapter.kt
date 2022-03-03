package com.example.project_uqac.ui.home

import android.R.attr
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.squareup.picasso.Picasso
import java.util.*
import android.graphics.drawable.Drawable
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.R.attr.src
import java.io.IOException
import java.net.HttpURLConnection


class PostAdapter(val posts: ArrayList<Post>, seed: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val random: Random = Random(seed.toLong())

    override fun getItemViewType(position: Int): Int {
        return R.layout.frame_textview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.frame_textview, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.getView(random.nextInt().toString())
        (holder as PostViewHolder).username.text = posts[position].username
        holder.text.text = posts[position].text
//        val bob = Picasso.get().load(posts[position].photo)

//        val url = URL(posts[position].photo)
//        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//        connection.setDoInput(true)
//        connection.getRequestMethod()
//        connection.connect()
//        val input: InputStream = connection.getInputStream()
//        val myBitmap = BitmapFactory.decodeStream(input)
//        val urlImage:URL = URL(posts[position].photo)
//        holder.photo.setImageBitmap(urlImage.toBitmap())
        Picasso.get().load(posts[position].photo).into(holder.photo)
    }

    fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e: IOException){
            null
        }
    }

    override fun getItemCount(): Int {
        return 100
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val text: TextView = itemView.findViewById(R.id.text)
        val photo: ImageView = itemView.findViewById(R.id.photo)
    }

}