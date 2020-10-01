package com.mathgeniusguide.quicknotes.util

import com.google.firebase.database.DatabaseReference
import com.mathgeniusguide.quicknotes.database.Note

object FirebaseFunctions {
    fun createNote(time: String, content: String, tags: String, database: DatabaseReference, uid: String): String? {
        val newItem = database.child(Constants.NOTES).child(uid).push()
        val note = Note.create()
        note.id = newItem.key
        note.time = time
        note.content = content
        note.tags = tags
        newItem.setValue(note)
        return note.id
    }

    fun updateNote(itemKey: String, time: String, content: String, tags: String, database: DatabaseReference, uid: String) {
        val itemReference = database.child(Constants.NOTES).child(uid).child(itemKey)
        itemReference.child("time").setValue(time)
        itemReference.child("content").setValue(content)
        itemReference.child("tags").setValue(tags)
    }

    fun updateNoteTime(itemKey: String, time: String, database: DatabaseReference, uid: String) {
        val itemReference = database.child(Constants.NOTES).child(uid).child(itemKey)
        itemReference.child("time").setValue(time)
    }

    fun updateNoteContent(itemKey: String, content: String, database: DatabaseReference, uid: String) {
        val itemReference = database.child(Constants.NOTES).child(uid).child(itemKey)
        itemReference.child("content").setValue(content)
    }

    fun updateNoteTags(itemKey: String, tags: String, database: DatabaseReference, uid: String) {
        val itemReference = database.child(Constants.NOTES).child(uid).child(itemKey)
        itemReference.child("tags").setValue(tags)
    }

    fun deleteNote(itemKey: String, database: DatabaseReference, uid: String) {
        val itemReference = database.child(Constants.NOTES).child(uid).child(itemKey)
        itemReference.removeValue()
    }
}