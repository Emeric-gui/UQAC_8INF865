package com.example.project_uqac.ui.chat

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class MessageAdapter(context: Context) : BaseAdapter() {
    var messages: MutableList<Message> = ArrayList()
    var context: Context = context
    var inflator : LayoutInflater?=null

    init {
        inflator = LayoutInflater.from(context)
    }

    fun add(message: Message) {
        messages.add(message)
        notifyDataSetChanged() // to render the list we need to notify
    }



    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(i: Int): Any {
        return messages[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    override fun getView(i: Int, convertView: View, parent: ViewGroup?): View {
        var convertView: View? = convertView
        val holder = MessageViewHolder()
        val messageInflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val message = messages[i]
        if (message.isBelongsToCurrentUser) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(com.example.project_uqac.R.layout.my_message, null)
            holder.messageBody = convertView?.findViewById(com.example.project_uqac.R.id.message_body)
            convertView.setTag(holder)
            holder.messageBody!!.text = message.text
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(com.example.project_uqac.R.layout.their_message, null)
            holder.avatar = convertView?.findViewById(com.example.project_uqac.R.id.avatar) as View
            holder.name = convertView.findViewById(com.example.project_uqac.R.id.name)
            holder.messageBody = convertView.findViewById(com.example.project_uqac.R.id.message_body)
            convertView.setTag(holder)
            holder.name!!.text = message.memberData.getName()
            holder.messageBody!!.text = message.text
            val drawable = holder.avatar!!.background as GradientDrawable
            drawable.setColor(Color.parseColor(message.memberData.getColor()))
        }
        return convertView
    }

}

internal class MessageViewHolder {
    var avatar: View? = null
    var name: TextView? = null
    var messageBody: TextView? = null
}