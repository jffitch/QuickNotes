package com.mathgeniusguide.quicknotes.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.database.Note
import com.mathgeniusguide.quicknotes.database.Tag
import com.mathgeniusguide.quicknotes.util.FirebaseFunctions
import kotlinx.android.synthetic.main.multiple_notes_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class MultipleNotesFragment : Fragment() {
    lateinit var act: MainActivity
    lateinit var alert: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.multiple_notes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitBU.setOnClickListener {
            submitClicked();
        }

        alert = AlertDialog.Builder(context)
    }

    fun submitClicked() {
        val content = notesET.text.toString()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        val notes = content.trim().split("\n").filter { it.isNotBlank() }
        alert.setTitle(R.string.multiple_notes)
        alert.setMessage(String.format(resources.getString(R.string.multiple_notes_alert), notes.size))
        alert.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            for (i in notes) {
                val id = FirebaseFunctions.createNote(time, i, "", act.database, act.firebaseUser?.uid ?: act.ANONYMOUS)
                notesET.setText("")
                val note = Note.create()
                note.id = id
                note.content = i
                note.time = time
                note.tags = ""
                act.noteList.add(note)
            }
            act.noteList.sortByDescending { it.time }
            Toast.makeText(context, "Your notes have been added.", Toast.LENGTH_LONG).show()
        })
        alert.setNegativeButton(R.string.no, null)
        alert.show()
    }
}