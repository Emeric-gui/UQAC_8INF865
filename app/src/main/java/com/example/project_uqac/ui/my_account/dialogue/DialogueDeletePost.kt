package com.example.project_uqac.ui.my_account.dialogue

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.tabs.MyAccountTabMyPosts
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DialogueDeletePost:DialogFragment() {

    private lateinit var articleToDel : String
    private lateinit var myCaller : MyAccountTabMyPosts

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.suppression_post)
        builder.setMessage(R.string.verification_suppression_post)

        builder.setCancelable(false)
        builder.setNegativeButton(getString(R.string.annuler), null)
        builder.setPositiveButton(getString(R.string.confirmer)) { _: DialogInterface, _: Int ->
            val db = Firebase.firestore
            db.collection("Articles").document(articleToDel)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    myCaller.gonnaLoad()
                }
        }

        return builder.create()
    }

    fun arguments(elem : String, caller : MyAccountTabMyPosts) {
        articleToDel = elem
        myCaller = caller

    }
}