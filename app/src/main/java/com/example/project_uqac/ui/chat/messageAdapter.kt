package com.example.project_uqac.ui.chat

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.project_uqac.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase

import com.example.project_uqac.ui.chat.ChatFragment.Companion.ANONYMOUS
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide

import com.example.project_uqac.databinding.ImageMessageBinding
import com.example.project_uqac.databinding.MessageBinding
import com.google.firebase.storage.ktx.storage


class MessageAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val currentUserName: String?
) :
    FirebaseRecyclerAdapter<Message, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].messageText != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].messageText != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.messageTextView.text = item.messageText
            setTextColor(item.messageUser, binding.messageTextView)

            binding.messengerTextView.text = if (item.messageUser == null) ANONYMOUS else item.messageUser

            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }

        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                textView.setTextColor(Color.WHITE)
                textView.gravity= Gravity.RIGHT
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
                textView.gravity= Gravity.LEFT
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) {
           loadImageIntoView(binding.messageImageView, item.imageUrl!!)

            binding.messengerTextView.text = if (item.messageUser == null) ANONYMOUS else item.messageUser
            if (item.photoUrl != null) {
                val a = item.photoUrl
                Log.d(ChatFragment.TAG, "url: $a")
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        Log.i("IMAGE","URL = $url")
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.i("downloadUrl","URL = ${downloadUrl}")
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}

