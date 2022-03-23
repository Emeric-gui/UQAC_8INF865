package com.example.project_uqac.ui.home.popupDiscussion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentSearchBinding
import com.example.project_uqac.ui.discussions.DiscussionsFragment
import kotlinx.android.synthetic.main.fragment_popup_home.view.*



class DialogFragmentDiscussion:DialogFragment() {

    private var _binding: FragmentSearchBinding? = null

    private var rootView: View? = null

    private val binding get() = _binding!!

    private lateinit var texte : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
            View? {
        rootView = inflater.inflate(R.layout.fragment_popup_home, container, false)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rootView?.button_home_annuler?.setOnClickListener(){
            dismiss()
        }

        rootView?.button_home_contacter?.setOnClickListener(){
            Log.println(Log.DEBUG, "debug", "Appui sur contacter")
            dismiss()
//            val fr = parentFragmentManager.beginTransaction()
//            fr.add(R.id.nav_host_fragment_activity_main, DiscussionsFragment())
//            fr.replace(R.id.nav_host_fragment_activity_main, DiscussionsFragment())
//            fr.commit()
        }

        val texteZone : TextView = rootView?.message_fragment_popup as TextView
        texteZone.text = texte

    }

    fun addInfos(lieu : String, objet : String, date : String){
        texte = "Recapitulatif : \n-Objet : $objet\n-Lieu : $lieu\n-Date : $date"
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}