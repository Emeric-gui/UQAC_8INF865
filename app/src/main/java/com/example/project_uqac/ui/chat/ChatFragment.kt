package com.example.project_uqac.ui.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentChatBinding
import com.example.project_uqac.ui.conversation.Conversation
import com.example.project_uqac.ui.conversation.ConversationsAdapter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.time.format.DateTimeFormatter

import com.example.project_uqac.ui.chat.MyButtonObserver
import com.example.project_uqac.ui.my_account.MyAccountLogin



class ChatFragment : Fragment(){

    private lateinit var _context: Context
    private lateinit var dashboardViewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null
    private lateinit var manager: LinearLayoutManager

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        //onImageSelected(uri)
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
             Toast.makeText(
                 _context,
                 "Latitude:",
                 Toast.LENGTH_SHORT
             ).show()
             val fr = parentFragmentManager.beginTransaction()
             fr.replace(R.id.nav_host_fragment_activity_main, MyAccountLogin())
             fr.commit()
             // Not signed in, launch the Sign In activity
             //startActivity(Intent(this, SignInActivity::class.java))
             return root
         }

        // Initialize Realtime Database and FirebaseRecyclerAdapter
        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()

        adapter = MessageAdapter(options, getUserName())
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(_context)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver for details
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )
        // Disable the send button when there's no text in the input field
        // See MyButtonObserver for details
        binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

        // When the send button is clicked, send a text message
        binding.sendButton.setOnClickListener {
            if(binding.messageEditText.text.toString()!="")
            {
                val friendlyMessage = Message(
                    binding.messageEditText.text.toString(),
                    getUserName(),
                    getPhotoUrl(),
                    null /* no image */
                )
                db.reference.child(MESSAGES_CHILD).push().setValue(friendlyMessage)
            }
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            //openDocument.launch(arrayOf("image/*"))
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context=context
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val root: View = binding.root

        messageAdapter = MessageAdapter(_context)
        messagesView = root.findViewById(R.id.messages_view)
        messagesView.adapter = messageAdapter*/
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    public override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }



    /*private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = Message(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
        db.reference
            .child(MESSAGES_CHILD)
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
    }*/

    /*private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(
                _context
            ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val friendlyMessage =
                            Message(null, getUserName(), getPhotoUrl(), uri.toString())
                        db.reference
                            .child(MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(friendlyMessage)
                    }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    TAG,
                    "Image upload task was unsuccessful.",
                    e
                )
            }
    }*/

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


    /*private fun displayChatMessages(root :  View)
    {
        /*Toast.makeText(context, "Root : $root",
            Toast.LENGTH_SHORT).show()*/
        val listOfMessages = root.findViewById(R.id.list_of_messages) as ListView

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        db = FirebaseDatabase.getInstance();
        databaseReference = db!!.getReference();
       // databaseReference!!.keepSynced(true);

        //firebaseDatabase = Firebase.database
        val messagesRef = db!!.reference.child(MESSAGES_CHILD)

        /*Toast.makeText(context, "databaseReference : $databaseReference ,databaseReference : $messagesRef ",
            Toast.LENGTH_SHORT).show()*/

        val options: FirebaseListOptions<Message> = FirebaseListOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .setLayout(R.layout.message)
            .build()

        Toast.makeText(context, "options = $options",
            Toast.LENGTH_SHORT).show()

        adapter = object : FirebaseListAdapter<Message>(options) {
            override fun populateView(v: View, model: Message, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                var a =model.messageText
                var b = model.messageUser
                Toast.makeText(context,
                    "text : $a , user : $b",
                    Toast.LENGTH_SHORT).show()
                messageText.setText(model.messageText)
                messageUser.setText(model.messageUser)

                // Format the date before showing it
                var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
                messageTime.setText(
                    DateFormat.format(
                        "dd-MM-yyyy (HH:mm:ss)",
                        model.messageTime)
                )
            }
        }

        listOfMessages.adapter = adapter
    }*/

    fun arguments(args: Bundle) {
        val objet = args.getString("objet")
        val lieu = args.getString("lieu")
        val date = args.getString("date")
        val nom = args.getString("nom")
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}