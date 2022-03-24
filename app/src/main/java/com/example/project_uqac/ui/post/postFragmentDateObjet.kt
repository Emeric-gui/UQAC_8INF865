package com.example.project_uqac.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.type.Date
import java.text.SimpleDateFormat


class PostFragmentDateObjet : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_date_objet, container, false)

        val textModel = requireArguments().getString("model")
        val textMarque = requireArguments().getString("marque")
        val textDescription = requireArguments().getString("description")
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 80
        var varMonth: Int = 0
        var calendar: CalendarView = view.findViewById(R.id.calendarView_post_objet)

        val sdf = SimpleDateFormat("yyyyMMdd")
        var textDate: String = sdf.format(calendar.date)

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            varMonth = month + 1
            if (varMonth <= 9)
            {
                val m : String = "0$varMonth";
                if (dayOfMonth <= 9) {
                    val d : String = "0$dayOfMonth";
                    textDate= "$year$m$d"
                    }
                else
                {
                textDate= "$year$m$dayOfMonth"
                }
            }
            else if (dayOfMonth <= 9) {
                val d : String = "0$dayOfMonth";
                textDate= "$year$varMonth$d"
            }
            else
            {
                textDate= "$year$varMonth$dayOfMonth"
            }
        }

        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevDateObjet)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDescriptionObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextDateObjet)
        buttonNext.setOnClickListener(){

            val fragment = PostFragmentLieuObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            args.putString("description", textDescription.toString())
            args.putInt("date", textDate.toInt())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }



        return view

    }

}