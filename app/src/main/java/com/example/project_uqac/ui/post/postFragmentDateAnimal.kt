package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import java.text.SimpleDateFormat

class PostFragmentDateAnimal : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_date_animal, container, false)

        val textSpecie = requireArguments().getString("specie")
        val textRace = requireArguments().getString("race")
        val textDescription = requireArguments().getString("description")
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 80
        var varMonth: Int = 0
        var calendar: CalendarView = view.findViewById(R.id.calendarView_post_animal)

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

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevDateAnimal)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDescriptionAnimal()
            args.putString("specie", textSpecie.toString())
            args.putString("race", textRace.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextDateAnimal)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentLieuAnimal()
            args.putString("specie", textSpecie.toString())
            args.putString("race", textRace.toString())
            args.putString("description", textDescription.toString())
            args.putInt("date", textDate.toInt())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view
    }

}