package com.mathgeniusguide.quicknotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.database.Note
import kotlinx.android.synthetic.main.note_list_item.view.*

class NoteListAdapter (private val items: List<Note>, val act: MainActivity, val navController: NavController) : RecyclerView.Adapter<NoteListAdapter.ViewHolder> () {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(act.applicationContext).inflate(R.layout.note_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        holder.note.text = i.content
        holder.date.text = i.time
        holder.tags.text = (i.tags ?: "").split(",").map {"#${it}"}.joinToString(" ")
        holder.parent.setOnClickListener {
            act.noteSelected = i
            navController.navigate(R.id.action_edit)
        }
    }

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val note = view.note
        val date = view.date
        val tags = view.tags
        val parent = view
    }
}