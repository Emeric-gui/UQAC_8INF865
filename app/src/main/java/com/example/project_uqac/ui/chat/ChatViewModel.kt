package com.example.project_uqac.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is Post Fragment"
    }
    val text: LiveData<String> = _text
}