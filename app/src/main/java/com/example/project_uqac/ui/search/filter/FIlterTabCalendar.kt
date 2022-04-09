package com.example.project_uqac.ui.search.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import java.text.SimpleDateFormat

class FIlterTabCalendar(dialogueContext: DialogueFragmentFilter) : Fragment() {
    private var dialogueContext : DialogueFragmentFilter = dialogueContext

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_filter_calendrier, container, false)

        var varMonth: Int = 0
        var calendar: CalendarView = view.findViewById(R.id.calendarView)

        val sdf = SimpleDateFormat("yyyyMMdd")
        var textDate: String = sdf.format(calendar.date) //sdf.format(calendar.date)

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
            dialogueContext.setDate(textDate.toInt())
        }

        dialogueContext.setDate(textDate.toInt())

        return view
    }
}