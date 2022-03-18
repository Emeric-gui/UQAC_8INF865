package com.example.project_uqac.ui.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import kotlinx.android.synthetic.main.fragment_filter.view.*

class DialogueFragmentFilter:DialogFragment()  {

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
   View? {
      var rootView : View = inflater.inflate(R.layout.fragment_filter, container, false)

         rootView.button_valider.setOnClickListener(){
             dismiss()
         }

      return rootView
   }
   }
