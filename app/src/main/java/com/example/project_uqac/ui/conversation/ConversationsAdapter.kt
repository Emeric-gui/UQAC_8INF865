package com.example.project_uqac.ui.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.squareup.picasso.Picasso


class ConversationsAdapter (private val mConversations: List<Conversation>) : RecyclerView.Adapter<ConversationsAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val titleObjectTextView: TextView = itemView.findViewById<TextView>(R.id.itemTitreObjectConversation)
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.itemNameConversation)
        val lastMessageTextView: TextView = itemView.findViewById<TextView>(R.id.itemLastMessageConversation )
        val imageObjectImageView: ImageView = itemView.findViewById<ImageView>(R.id.imageViewConversation)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val conversationView = inflater.inflate(R.layout.item_conversation, parent, false)
        // Return a new holder instance
        return ViewHolder(conversationView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val conversation: Conversation = mConversations[position]
        // Set item views based on your views and data model
        val titleView = viewHolder.titleObjectTextView
        titleView.text = conversation.titleObject
        val lieuView = viewHolder.nameTextView
        lieuView.text = conversation.name
        val dateView = viewHolder.lastMessageTextView
        dateView.text = conversation.lastMessage
        Picasso.get().load(conversation.image).into(viewHolder.imageObjectImageView)

    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mConversations.size
    }
}
