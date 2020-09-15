package com.mathgeniusguide.quicknotes.database

class Note {
    companion object Factory {
        fun create(): Note = Note()
    }
    var id: String? = null
    var time: String? = null
    var content: String? = null
    var tags: String? = null
}