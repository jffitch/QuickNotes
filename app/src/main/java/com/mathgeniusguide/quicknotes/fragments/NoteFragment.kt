package com.mathgeniusguide.quicknotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.util.FirebaseFunctions
import kotlinx.android.synthetic.main.note_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment: Fragment() {
    lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.note_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteET.requestFocus()

        submitBU.setOnClickListener {
            submitClicked();
        }
    }

    fun submitClicked() {
        val content = noteET.text.toString()
        val tags = tagsET.text.toString().replace(Regex(" *, *"),",").trim()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        FirebaseFunctions.createNote(time, content, tags, act.database)
        Toast.makeText(context, "Your note has been added.", Toast.LENGTH_LONG).show()
        noteET.setText("")
        tagsET.setText("")
    }
}