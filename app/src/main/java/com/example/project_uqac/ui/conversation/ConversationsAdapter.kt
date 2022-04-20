package com.example.project_uqac.ui.conversation

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project_uqac.R


import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ConversationsAdapter(context:Context, private val discussionsList:ArrayList<Conversation>?) : BaseAdapter()
{
    var inflator : LayoutInflater?=null

    init {
        inflator = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?=null
        view = inflator?.inflate(R.layout.item_conversation,parent,false)

        val txtTitle: TextView = view?.findViewById(R.id.itemTitreObjectConversation) as TextView
        val txtName : TextView = view.findViewById(R.id.itemNameConversation) as TextView
        val txtLastMessage : TextView = view.findViewById(R.id.itemLastMessageConversation) as TextView
        val imageObject :ImageView = view.findViewById(R.id.imageViewConversation) as ImageView

        val conversation : Conversation = getItem(position)!!

        txtTitle.setText(conversation.titleObject)
        if(conversation.user1Mail == Firebase.auth.currentUser?.email.toString())
        {
            txtName.setText(conversation.user2)
            conversation.user2Mail?.let { showImage(it,imageObject) }

        }else
        {
            txtName.setText(conversation.user1)
            conversation.user1Mail?.let { showImage(it,imageObject) }
        }

        txtLastMessage.setText(conversation.lastMessage)

        return view
    }

    override fun getCount(): Int {
        return discussionsList!!.size
    }

    override fun getItem(position: Int): Conversation? {
        return discussionsList?.get(count - 1 - position)
        //return getItem(getCount() - 1 - position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun showImage(userMail:String, imageView: ImageView){
        val storageRef = FirebaseStorage.getInstance().getReference("profil_pics/"+userMail)
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            Log.d("MyAccountLogged : ", "No profil pic in database")
        }
    }
}