package com.mathgeniusguide.quicknotes.fragments

import android.app.AlertDialog
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
        val id = FirebaseFunctions.createNote(time, content, "", act.database, act.firebaseUser?.uid ?: act.ANONYMOUS)
        Toast.makeText(context, "Your note has been added.", Toast.LENGTH_LONG).show()
        notesET.setText("")
        val note = Note.create()
        note.id = id
        note.content = content
        note.time = time
        note.tags = ""
        act.noteList.add(note)
        for (tag in (note.tags ?: "").split(",")) {
            act.tagList.add(Tag(tag, false))
        }
        act.tagList = act.tagList.distinctBy { it.id }.filter { it.id.length <= 20 && it.id.isNotEmpty() }
            .toMutableList()
        act.tagList.sortBy { it.id }
        act.noteList.sortByDescending { it.time }
    }
}