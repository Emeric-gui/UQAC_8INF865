package com.example.project_uqac.ui.chat

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentChatBinding
import com.example.project_uqac.ui.my_account.MyAccountLogin
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


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
        binding.messageRecyclerView.itemAnimator=null
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


    override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    override fun onResume() {
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

        val timestamp = Date().time
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
        getToken(friendlyMessage.messageText!!)
    }

    fun arguments(args: Bundle) {
        idChat = args.getString("IDChat").toString()
        idConv = args.getString("IDConv").toString()
        idUser = args.getString("IDUser").toString()
        idOtherUser = args.getString("IDOtherUser").toString()
    }

    private fun getToken(message: String) {
        val friendlyMessage = Message(
            binding.messageEditText.text.toString(),
            getUserName(),
            getPhotoUrl(),
            null /* no image */
        )
        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(idOtherUser)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot){
                if(snapshot.exists()){
                    val token = snapshot.child("token").value.toString()
                    //val name = snapshot.child("name").value.toString()
                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("title", friendlyMessage.messageUser)
                    data.put("message", friendlyMessage.messageText)
                    data.put("hisId", idUser)
                    data.put("chatId", idConv)

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun sendNotification(to : JSONObject){
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            NOTIFICATION_URL,
            to,
            Response.Listener { response : JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Log.d("TAG", "onError: $it")
            }){
            override fun getHeaders() : MutableMap<String,String> {
                val map: MutableMap<String,String> = HashMap()

                map["Authorization"] = "key=" + SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue : RequestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }

    companion object {
        const val TAG = "MainActivity"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
        const val NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send"
        const val SERVER_KEY = "AAAAjSZbcsc:APA91bHpfAKw-GJrVwl_G_HmzvwZXvBMec2MkAIRhoT65DWvSZZTK65eCcevBy7PuMc8J7fvhjN7gRy5DTtPNfAy-yqUmxLOdyhFx3JJ1JRXfMyTeZdwkj2xXhgqfIfaXtIzrxlfyBxD"
    }

}