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
import com.mathgeniusguide.quicknotes.util.FirebaseFunctions
import kotlinx.android.synthetic.main.note_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment: Fragment() {
    lateinit var act: MainActivity
    lateinit var alert: AlertDialog.Builder
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

        saveBU.setOnClickListener {
            saveClicked();
        }

        deleteBU.setOnClickListener {
            deleteClicked();
        }

        if (act.noteSelected.id.isNullOrEmpty()) {
            saveBU.visibility = View.GONE
            deleteBU.visibility = View.GONE
        } else {
            submitBU.visibility = View.GONE
            noteET.setText(act.noteSelected.content)
            tagsET.setText(act.noteSelected.tags)
        }

        alert = AlertDialog.Builder(context)
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

    fun saveClicked() {
        val content = noteET.text.toString()
        val tags = tagsET.text.toString().replace(Regex(" *, *"),",").trim()
        alert.setTitle(R.string.save_note)
        alert.setMessage(R.string.save_alert)
        alert.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            FirebaseFunctions.updateNote(act.noteSelected.id ?: "", act.noteSelected.time ?: "", content, tags, act.database)
            Toast.makeText(context, "Your note has been edited.", Toast.LENGTH_LONG).show()
        })
        alert.setNegativeButton(R.string.no, null)
        alert.show()
    }

    fun deleteClicked() {
        alert.setTitle(R.string.delete_note)
        alert.setMessage(R.string.delete_alert)
        alert.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            FirebaseFunctions.deleteNote(act.noteSelected.id ?: "", act.database)
            Toast.makeText(context, "Your note has been deleted.", Toast.LENGTH_LONG).show()
            noteET.setText("")
            tagsET.setText("")
            saveBU.visibility = View.GONE
            deleteBU.visibility = View.GONE
            submitBU.visibility = View.VISIBLE
        })
        alert.setNegativeButton(R.string.no, null)
        alert.show()
    }
}