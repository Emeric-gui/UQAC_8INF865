package com.example.project_uqac.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiscussionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Discussions Fragment"
    }
    val text: LiveData<String> = _text
}