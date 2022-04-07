package com.example.project_uqac.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
//import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentChatBinding
import com.example.project_uqac.ui.discussions.DiscussionsFragment
import com.example.project_uqac.ui.my_account.MyAccountLogin
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*


class ChatFragment : Fragment(){

    private lateinit var _context: Context
    private lateinit var dashboardViewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null
    private lateinit var manager: LinearLayoutManager

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private  var adapter: MessageAdapter? = null

    private lateinit var idChat: String
    private lateinit var idConv: String
    private lateinit var idUser: String
    private lateinit var idOtherUser: String


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        onImageSelected(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

         auth = Firebase.auth

         if (Firebase.auth.currentUser == null) {
             val fr = parentFragmentManager.beginTransaction()
             fr.replace(R.id.nav_host_fragment_activity_main, MyAccountLogin())
             fr.commit()
             return root
         }

        // Initialize Realtime Database and FirebaseRecyclerAdapter
        db = Firebase.database

        /*idChat = bundle.getString("IDChat") // may change by the data
        idConv = bundle.getString("IDConv")
        idUser = bundle.getString("IDUser")
        idOtherUser = bundle.getString("IDOtherUser")*/

        val messagesRef = idChat.let { db.reference.child("Chat").child(it).child("Messages") }

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = messagesRef.let {
            FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(it, Message::class.java)
                .build()
        }

        adapter = options.let { MessageAdapter(it, getUserName()) }
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(_context)
        manager.stackFromEnd = true
        binding.messageRecyclerView.itemAnimator=null;
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver for details
        adapter!!.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter!!, manager)
        )
        // Disable the send button when there's no text in the input field
        // See MyButtonObserver for details
        binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

        // When the send button is clicked, send a text message
        binding.sendButton.setOnClickListener {
            if(binding.messageEditText.text.toString()!="")
            {
                sendData()
                binding.messageEditText.setText("")
            }
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context=context
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }


    public override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    public override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }


    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = Message(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
        idConv.let { it1 ->
            db.reference.child("Conversations").child(it1).child("lastMessage").setValue("Image")
        }
        idChat.let {
            db.reference
                .child("Chat").child(it).child("Messages")
                .push()
                .setValue(
                    tempMessage,
                    DatabaseReference.CompletionListener { databaseError, databaseReference ->
                        if (databaseError != null) {
                            Log.w(
                                TAG, "Unable to write message to database.",
                                databaseError.toException()
                            )
                            return@CompletionListener
                        }

                        // Build a StorageReference and then upload the file
                        val key = databaseReference.key
                        val storageReference = Firebase.storage
                            .getReference(user!!.uid)
                            .child(key!!)
                            .child(uri.lastPathSegment!!)
                        putImageInStorage(storageReference, uri, key)
                    })
        }
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        activity?.let {
            storageReference.putFile(uri)
                .addOnSuccessListener(
                    it
                ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                    // and add it to the message.
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val friendlyMessage =
                                Message(null, getUserName(), getPhotoUrl(), uri.toString())
                            idChat.let { it1 ->
                                db.reference
                                    .child("Chat").child(it1).child("Messages")
                                    .child(key!!)
                                    .setValue(friendlyMessage)
                            }
                        }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    Log.w(
                        TAG,
                        "Image upload task was unsuccessful.",
                        e
                    )
                }
        }
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    private fun sendData(){
        val friendlyMessage = Message(
            binding.messageEditText.text.toString(),
            getUserName(),
            getPhotoUrl(),
            null /* no image */
        )
        idChat.let { db.reference.child("Chat").child(it).child("Messages").push().setValue(friendlyMessage)}

        var timestamp = Date().time
        idConv.let { it1 -> db.reference.child("Conversations").child(it1).child("timestamp").setValue(timestamp) }
        idUser.let { it1 ->
            idConv.let { it2 ->
                db.reference.child("Users").child(it1).child("Conversations")
                    .child(it2).child("timestamp").setValue(timestamp)
            }
        }
        idOtherUser.let { it1 ->
            idConv.let { it2 ->
                db.reference.child("Users").child(it1).child("Conversations")
                    .child(it2).child("timestamp").setValue(timestamp)
            }
        }
        idConv.let { it1 ->
            db.reference.child("Conversations").child(it1).child("lastMessage").setValue(binding.messageEditText.text.toString())
        }
    }

    fun arguments(args: Bundle) {
        idChat = args.getString("IDChat").toString()
        idConv = args.getString("IDConv").toString()
        idUser = args.getString("IDUser").toString()
        idOtherUser = args.getString("IDOtherUser").toString()
    }

    companion object {
        const val TAG = "MainActivity"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}