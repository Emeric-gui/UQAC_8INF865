package com.example.project_uqac.ui.conversation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.squareup.picasso.Picasso


import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class ConversationsAdapter(context:Context, private val discussionsList:ArrayList<Conversation>?) : BaseAdapter()
{
    var inflator : LayoutInflater?=null

    init {
        inflator = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?=null
        view = inflator?.inflate(R.layout.item_conversation,parent,false)

        val txtTitle: TextView
        val txtName : TextView
        val txtLastMessage : TextView
        val imageObject :ImageView

        txtTitle = view?.findViewById(R.id.itemTitreObjectConversation) as TextView
        txtName = view?.findViewById(R.id.itemNameConversation) as TextView
        txtLastMessage = view?.findViewById(R.id.itemLastMessageConversation) as TextView
        imageObject = view?.findViewById(R.id.imageViewConversation) as ImageView

        var conversation : Conversation

        conversation=getItem(position)!!

        txtTitle.setText(conversation.titleObject)
        if(conversation.user1 == Firebase.auth.currentUser?.displayName)
        {
            txtName.setText(conversation.user2)
        }else
        {
            txtName.setText(conversation.user1)
        }

        txtLastMessage.setText(conversation.lastMessage)
        //Picasso.get().load(conversation.imageURL).into(imageObject)
        return view!!
    }

    override fun getCount(): Int {
        return discussionsList!!.size
    }

    override fun getItem(position: Int): Conversation? {
        return discussionsList?.get(getCount() - 1 - position)
        //return getItem(getCount() - 1 - position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}