package com.example.project_uqac.ui.home.popupDiscussion

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.project_uqac.R
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.conversation.Conversation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class DialogFragmentDiscussion:DialogFragment() {

    private lateinit var mObjet : String
    private lateinit var mLieu : String
    private lateinit var mDate : String
    private lateinit var mNom : String
    private lateinit var mMail : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val texte = "Recapitulatif : \n-Objet : $mObjet\n-Lieu : $mLieu\n-Date : $mDate" +
                "\nContact : $mNom"

        var listIdConv1 : ArrayList<String>? = ArrayList()
        var listIdConv2 : ArrayList<String>? = ArrayList()

        var user1ID: String? = null
        var user2ID: String? = null

        var IDConv: String? = null
        var IDChat: String? = null
        var haveAConv: Boolean = false

        builder.setMessage(texte)
        builder.setTitle(R.string.verification_contact)

        builder.setCancelable(false)
        builder.setNegativeButton(getString(R.string.annuler), null)
        builder.setPositiveButton(getString(R.string.contacter)) { _: DialogInterface, _: Int ->
            if(mMail!=Firebase.auth.currentUser?.email){
                var fm = parentFragment?.parentFragmentManager
                var fr = fm?.beginTransaction()

                var discuFrag = fm?.findFragmentById(R.id.navigation_discussions)
                if (discuFrag != null) {
                    fr?.show(discuFrag)
                }else{
                    var chat = ChatFragment()

                    Firebase.database.reference.get().addOnSuccessListener {
                        //We get the two users id
                        it.child("Users_ID").getValue<Map<String,String>>()!!.forEach {
                            if(it.value==Firebase.auth.currentUser?.email){
                                user1ID=it.key
                            }
                            if(it.value==mMail){
                                user2ID=it.key
                            }
                        }

                        Log.i("VALUE","USER ID 1 = $user1ID & USER ID 2 = $user2ID")

                        //Add id conv 's user1  to the idconv1 list
                        user1ID?.let { it1 -> it.child("Users").child(it1).child("Conversations").children.forEach {
                            it.key?.let { it2 -> listIdConv1?.add(it2) }
                        }}

                        //Add id conv 's user2  to the idconv2 list
                        user2ID?.let { it1 -> it.child("Users").child(it1).child("Conversations").children.forEach {
                            it.key?.let { it2 -> listIdConv2?.add(it2) }
                        }}
                        //Log.i("TEST VALUE","TEST ${user1ID?.let { it1 -> it.child("Users").child(it1).child("Conversations").children.forEach{

                        listIdConv1?.forEach { Log.i("VALUE CONV 1","ID CONV = $it") }
                        listIdConv2?.forEach { Log.i("VALUE CONV 2","ID CONV = $it") }

                        //Find a conv already created
                        listIdConv1?.forEach {
                            var id=it
                            listIdConv2?.forEach{
                                if(it==id){
                                    IDConv=it
                                    haveAConv=true
                                }
                            }
                        }
                        var timestamp = Date().time

                        // If no conv between users
                        if(haveAConv==false){
                            var ref = user1ID?.let { it1 -> Firebase.database.reference.child("Users").child(it1).child("Conversations") }
                            IDConv = ref?.push()?.key
                            IDChat = ref?.push()?.key
                            var map = mapOf<String,Long>("timestamp" to timestamp)
                            ref?.child(IDConv!!)?.setValue(map)
                            user2ID?.let { it1 -> Firebase.database.reference.child("Users").child(it1).child("Conversations").child(IDConv!!).setValue(map) }

                            var conv = Conversation(mObjet,Firebase.auth.currentUser?.displayName,mNom,Firebase.auth.currentUser?.email,mMail," ",IDChat, timestamp)
                            Firebase.database.reference.child("Conversations").child(IDConv!!).setValue(conv)
                        }else{
                            IDChat =
                                IDConv?.let { it1 -> it.child("Conversations").child(it1).child("chat").getValue<String>() }
                        }
                        var args = Bundle()
                        args.putString("IDChat", IDChat)
                        args.putString("IDConv", IDConv)
                        args.putString("IDUser", user1ID)
                        args.putString("IDOtherUser", user2ID)
                        chat.arguments(args)
                        fr?.replace(R.id.nav_host_fragment_activity_main,chat )
                        fr?.commit()
                    }
                }
            }else{
                Toast.makeText(context, "Il s'agit d'un de vos postes.", Toast.LENGTH_SHORT).show()
            }
        }
        return builder.create()
    }

    fun arguments(args: Bundle) {
        mObjet = args.getString("objet").toString()
        mLieu = args.getString("lieu").toString()
        mDate = args.getString("date").toString()
        mNom = args.getString("nom").toString()
        mMail = args.getString("mail").toString()
    }
}