package com.example.project_uqac.ui.chat

class MemberData {
    private var name: String? = null
    private var color: String? = null


    constructor(name: String?, color: String?) {
        this.name = name
        this.color = color
    }

    fun getName(): String? {
        return name
    }

    fun getColor(): String? {
        return color
    }

    // Add an empty constructor so we can later parse JSON into MemberData using Jackson
    constructor() {}
}
