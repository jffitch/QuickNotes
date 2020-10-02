package com.mathgeniusguide.quicknotes.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.adapter.TagAdapter
import com.mathgeniusguide.quicknotes.database.Note
import com.mathgeniusguide.quicknotes.database.Tag
import com.mathgeniusguide.quicknotes.util.FirebaseFunctions
import kotlinx.android.synthetic.main.note_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {
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

        setUpTags()

        act.firebaseLoaded.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                setUpTags()
            }
        })
    }

    fun setUpTags() {
        val tagsHalves = listOf(emptyList<Tag>().toMutableList(), emptyList<Tag>().toMutableList())
        var tagsHalf = 0
        for (i in act.tagList) {
            tagsHalves[tagsHalf].add(i)
            tagsHalf = 1 - tagsHalf
        }
        tagsRV0.layoutManager = LinearLayoutManager(context)
        tagsRV0.adapter = TagAdapter(tagsHalves[0], act)
        tagsRV1.layoutManager = LinearLayoutManager(context)
        tagsRV1.adapter = TagAdapter(tagsHalves[1], act)

        for (i in act.tagList) {
            i.checked = false
        }
    }

    fun submitClicked() {
        val content = noteET.text.toString()
        val tags = tagsET.text.toString().replace(Regex(" *, *"), ",").trim()
        val tagsSelected = act.tagList.filter { it.checked }.map {it.id}
        val tagsSelectedString = tagsSelected.joinToString(",")
        val tagsString = tags + (if (tags.isBlank() || tagsSelectedString.isBlank()) "" else ",") + tagsSelectedString
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        val id = FirebaseFunctions.createNote(time, content, tagsString, act.database, act.firebaseUser?.uid ?: act.ANONYMOUS)
        Toast.makeText(context, "Your note has been added.", Toast.LENGTH_LONG).show()
        noteET.setText("")
        tagsET.setText("")
        val note = Note.create()
        note.id = id
        note.content = content
        note.time = time
        note.tags = tagsString
        act.noteList.add(note)
        for (tag in (note.tags ?: "").split(",")) {
            act.tagList.add(Tag(tag, false))
        }
        act.tagList = act.tagList.distinctBy { it.id }.filter { it.id.length <= 20 && it.id.isNotEmpty() }
            .toMutableList()
        act.tagList.sortBy { it.id }
        act.noteList.sortByDescending { it.time }
    }

    fun saveClicked() {
        val content = noteET.text.toString()
        val tags = tagsET.text.toString().replace(Regex(" *, *"), ",").trim()
        val tagsSelected = act.tagList.filter { it.checked }.map {it.id}
        val tagsSelectedString = tagsSelected.joinToString(",")
        val tagsString = tags + (if (tags.isBlank() || tagsSelectedString.isBlank()) "" else ",") + tagsSelectedString
        alert.setTitle(R.string.save_note)
        alert.setMessage(R.string.save_alert)
        alert.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            FirebaseFunctions.updateNote(
                act.noteSelected.id ?: "",
                act.noteSelected.time ?: "",
                content,
                tagsString,
                act.database,
                act.firebaseUser?.uid ?: act.ANONYMOUS
            )
            val note = act.noteList.first { it.id == act.noteSelected.id ?: "" }
            note.content = content
            note.tags = tagsString
            for (tag in (note.tags ?: "").split(",")) {
                act.tagList.add(Tag(tag, false))
            }
            act.tagList = act.tagList.distinctBy { it.id }.filter { it.id.length <= 20 && it.id.isNotEmpty() }
                .toMutableList()
            act.tagList.sortBy { it.id }
            Toast.makeText(context, "Your note has been edited.", Toast.LENGTH_LONG).show()
        })
        alert.setNegativeButton(R.string.no, null)
        alert.show()
    }

    fun deleteClicked() {
        alert.setTitle(R.string.delete_note)
        alert.setMessage(R.string.delete_alert)
        alert.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            FirebaseFunctions.deleteNote(act.noteSelected.id ?: "", act.database, act.firebaseUser?.uid ?: act.ANONYMOUS)
            noteET.setText("")
            tagsET.setText("")
            saveBU.visibility = View.GONE
            deleteBU.visibility = View.GONE
            submitBU.visibility = View.VISIBLE
            act.noteList = act.noteList.filter { it.id != act.noteSelected.id }.toMutableList()
            Toast.makeText(context, "Your note has been deleted.", Toast.LENGTH_LONG).show()
        })
        alert.setNegativeButton(R.string.no, null)
        alert.show()
    }
}