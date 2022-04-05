//fichier DialogFragmentDelete.kt

package com.example.project_uqac.ui.my_account.popupDelete

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentMyAccountBinding
import kotlinx.android.synthetic.main.fragment_my_account_delete_item.view.*
import kotlinx.android.synthetic.main.fragment_popup_home.view.*


class DialogFragmentDelete:DialogFragment() {

    private var _binding: FragmentMyAccountBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
            View? {
        var rootView : View = inflater.inflate(R.layout.fragment_my_account_delete_item, container, false)

        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        rootView.button_my_account_cancel.setOnClickListener(){
            dismiss()
        }

        rootView.button_my_account_confirm.setOnClickListener(){
            Log.println(Log.DEBUG, "debug", "Appui sur contacter")
            dismiss()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}