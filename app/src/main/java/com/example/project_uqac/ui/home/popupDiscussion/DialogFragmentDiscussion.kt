//fichier DialogFragmentDelete.kt

package com.example.project_uqac.ui.home.popupDiscussion

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_popup_home.view.*


class DialogFragmentDiscussion:DialogFragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
            View? {
        var rootView : View = inflater.inflate(R.layout.fragment_popup_home, container, false)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        rootView.button_home_annuler.setOnClickListener(){
            dismiss()
        }

        rootView.button_home_contacter.setOnClickListener(){
            Log.println(Log.DEBUG, "debug", "Appui sur contacter")
            dismiss()
        }

        return rootView
    }

    @SuppressLint("SetTextI18n")
    fun addInfos(lieu : String, objet : String, date : String){
        var root: View = binding.root
        var textZone : TextView = root.findViewById<View>(R.id.message_fragment_popup) as TextView
        textZone.text = "Recapitulatif :$objet perdu à $lieu le $date"

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}