package com.example.project_uqac.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Post Fragment"
    }
    val text: LiveData<String> = _text

}