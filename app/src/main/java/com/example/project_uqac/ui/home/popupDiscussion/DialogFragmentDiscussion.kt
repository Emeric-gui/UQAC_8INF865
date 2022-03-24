package com.example.project_uqac.ui.home.popupDiscussion

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.project_uqac.R
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.discussions.DiscussionsFragment

class DialogFragmentDiscussion:DialogFragment() {

    private lateinit var mObjet : String
    private lateinit var mLieu : String
    private lateinit var mDate : String
    private lateinit var mNom : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val texte = "Recapitulatif : \n-Objet : $mObjet\n-Lieu : $mLieu\n-Date : $mDate" +
                "\nContact : $mNom"

        builder.setMessage(texte)
        builder.setTitle(R.string.verification_contact)

        builder.setCancelable(false)
        builder.setNegativeButton(getString(R.string.annuler), null)
        builder.setPositiveButton(getString(R.string.contacter)) { _: DialogInterface, _: Int ->
            var fm = parentFragment?.parentFragmentManager
            var fr = fm?.beginTransaction()

            var discuFrag = fm?.findFragmentById(R.id.navigation_discussions)
            if (discuFrag != null) {
                fr?.show(discuFrag)
            }else{
                var chat = ChatFragment()
                var args = Bundle()
                args.putString("objet", mObjet)
                args.putString("lieu", mLieu)
                args.putString("date", mDate)
                args.putString("nom", mNom)
                chat.arguments(args)
                fr?.replace(R.id.nav_host_fragment_activity_main, chat)
            }
            fr?.commit()
        }

        return builder.create()
    }

    fun arguments(args: Bundle) {
        mObjet = args.getString("objet").toString()
        mLieu = args.getString("lieu").toString()
        mDate = args.getString("date").toString()
        mNom = args.getString("nom").toString()
    }
}