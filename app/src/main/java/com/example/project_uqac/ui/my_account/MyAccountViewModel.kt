package com.example.project_uqac.ui.my_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyAccountViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is My Account Fragment"
    }
    val text: LiveData<String> = _text
}