package com.example.project_uqac.ui.discussions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
//import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentDiscussionsBinding
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.conversation.Conversation
import com.example.project_uqac.ui.conversation.ConversationsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class DiscussionsFragment : Fragment() {

    private lateinit var dashboardViewModel: DiscussionsViewModel
    private var _binding: FragmentDiscussionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var _context: Context
    private lateinit var listView: ListView

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ConversationsAdapter

    private var listIdConversation: ArrayList<String>? = ArrayList()
    private var listConversation: ArrayList<Conversation>?= ArrayList()
    private var mapIDConv: MutableMap<String,Conversation>? = mutableMapOf<String, Conversation>()
    private var mapIDTimestamp: MutableMap<String,Long?>? = mutableMapOf<String, Long?>()
    private  var userID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DiscussionsViewModel::class.java)

        _binding = FragmentDiscussionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth
        auth = Firebase.auth

        if (Firebase.auth.currentUser != null){

            listView = root.findViewById<ListView>(R.id.recipe_list_view)

            // Initialize Realtime Database and FirebaseRecyclerAdapter
            db = Firebase.database

            // We get the ID of the user
            db.reference.child("Users_ID").get().addOnSuccessListener {
                it.getValue<Map<String,String>>()!!.forEach{
                    if(it.value==auth.currentUser?.email){
                        userID=it.key
                    }
                }
                val userRef = userID?.let { it1 -> db.reference.child("Users").child(it1).child("Conversations").orderByChild("timestamp") }

                userRef?.addChildEventListener(object: ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot.key?.let { addConversation(it)}
                        //Log.i("Child Add ", " ${snapshot.key}")
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        //Log.i("Child ", "onChildChanged ${snapshot.child("timestamp").value}, name = ${snapshot.key}")
                        snapshot.key?.let { it1 -> refreshConversation(it1, snapshot.child("timestamp").getValue<Double>()) }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        //Log.i("Child ", "onChildRemoved ")
                        snapshot.key?.let { it1 -> removeConversation(it1) }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        //Log.i("Child ", "onChildMoved ")
                        //refreshConversation()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //Log.i("Child ", "onCancelled ")
                    }
                })
            }

            listView.setOnItemClickListener { parent, view, position, id ->
                sendInformations(position)
            }
        } else {
            Toast.makeText(
                context,
                "Vous devez vous connecter ...",
                Toast.LENGTH_SHORT
            ).show()
            this.findNavController().navigate(R.id.navigation_my_account)
            
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context=context
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ChatFragment.ANONYMOUS
    }

    private fun addConversation(id:String){
        // We check if we have already the id in the list
        if(listIdConversation?.contains(id) == false)
        {
            listIdConversation?.add(id)
            db.reference.child("Conversations").child(id).get().addOnSuccessListener {
                if(it.exists()){
                   // Log.i("Error","$it")
                    val conv = it.getValue<Conversation>()!!
                    if(listConversation?.contains(conv) == false){
                        listConversation?.add(conv)
                        mapIDConv?.set(id, conv)
                        mapIDTimestamp?.set(id,conv.timestamp)
                    }
                    //listConversation?.forEach { Log.i("add Conv", "${it.chat}") }
                    showConversations()
                }
            }
           // listConversation?.forEach { Log.i("test", "${it.chat}") }
        }else{
            Log.i("DEBUG ", "ID already here ")
        }
    }

    private fun removeConversation(id:String){
        if(listIdConversation?.contains(id) == true)
        {
            listIdConversation?.remove(id)
            listConversation?.remove(mapIDConv?.get(id))
            mapIDConv?.remove(id)
            showConversations()
        }
    }

    private fun refreshConversation(idChangedConv:String, timeChangedConv:Double?)
    {
        var list: ArrayList<String>? = ArrayList()

        listIdConversation?.remove(idChangedConv)
        listIdConversation?.forEach {
            list?.add(it)
        }

        listConversation?.remove(mapIDConv?.get(idChangedConv))
        listIdConversation?.clear()

        var i = 0
        var b:Boolean= true

        while(i< list?.size!!)
        {
            if((timeChangedConv!!<listConversation?.get(i)?.timestamp!!) && b==true){
                listIdConversation?.add(idChangedConv)
                b=false
            }else{
                listIdConversation?.add(list[i])
                i++
            }
        }
        if(b)
        {
            listIdConversation?.add(idChangedConv)
        }

        listConversation?.clear()

        listIdConversation?.forEach {
            var id = it
            db.reference.child("Conversations").child(id).get().addOnSuccessListener {
                if(it.exists()){
                    val conv = it.getValue<Conversation>()!!
                    if(listConversation?.contains(conv) == false){
                        listConversation?.add(conv)
                    }
                    showConversations()
                }
            }
        }
    }

    private fun sendInformations(position:Int)
    {
        var otherUserMail : String
        var otherUserID : String? = null

        //Get the name of the other player
        if(listConversation?.get(listConversation!!.size- 1 - position)?.user1Mail==auth.currentUser?.email){
            otherUserMail = listConversation?.get(listConversation!!.size- 1 - position)?.user2Mail.toString()
        }else{
            otherUserMail = listConversation?.get(listConversation!!.size- 1 - position)?.user1Mail.toString()
        }

        //Get the other user 's ID
        db.reference.child("Users_ID").get().addOnSuccessListener {

            it.getValue<Map<String, String>>()!!.forEach {
                if (it.value == otherUserMail) {
                    otherUserID = it.key
                }
            }

            var chat = ChatFragment()
            var args = Bundle()
            args.putString("IDChat", listConversation?.get(listConversation!!.size- 1 - position)?.chat)
            args.putString("IDConv", listIdConversation?.get(listConversation!!.size- 1 - position))
            args.putString("IDUser", userID)
            args.putString("IDOtherUser", otherUserID)
            chat.arguments(args)

            val fr = parentFragmentManager.beginTransaction()
            fr.replace(R.id.nav_host_fragment_activity_main, chat)
            fr.commit()
        }
    }

    private fun showConversations()
    {
        adapter = ConversationsAdapter(_context,listConversation)
        listView.adapter = adapter
    }


}

