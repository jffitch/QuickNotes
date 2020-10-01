package com.mathgeniusguide.quicknotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.adapter.TagAdapter
import com.mathgeniusguide.quicknotes.database.Tag
import kotlinx.android.synthetic.main.search_fragment.*
import java.util.*

class SearchFragment : Fragment() {
    lateinit var act: MainActivity
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
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTags()

        searchBU.setOnClickListener {
            searchClicked()
        }

        searchUntaggedBU.setOnClickListener {
            searchUntaggedClicked()
        }
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

    fun searchClicked() {
        val tagsSelected = act.tagList.filter { it.checked }.map {it.id}
        val tagsString = tagsSelected.joinToString(", ")
        val keywords = keywordET.text.toString().toLowerCase(Locale.getDefault()).trim(' ')
        act.noteListSelected = act.noteList
            .filter {(it.content ?: "").toLowerCase(Locale.getDefault()).contains(keywords)}
            .filter {tagsMatch((it.tags ?: "").split(","), tagsSelected)}
            .toMutableList()
        if (act.noteListSelected.isNotEmpty()) {
            if (tagsString.isEmpty()) {
                if (keywords.isEmpty()) {
                    act.searchDescription = resources.getString(R.string.all_posts)
                } else {
                    act.searchDescription = String.format(resources.getString(R.string.posts_involving), keywords)
                }
            } else {
                if (keywords.isEmpty()) {
                    act.searchDescription = String.format(resources.getString(R.string.posts_tagged), tagsString)
                } else {
                    act.searchDescription = String.format(resources.getString(R.string.posts_involving_and_tagged), keywords, tagsString)
                }
            }

            findNavController().navigate(R.id.action_search_to_list)
        } else {
            Toast.makeText(context, "No notes match your search.", Toast.LENGTH_LONG).show()
        }
    }

    fun searchUntaggedClicked() {
        val keywords = keywordET.text.toString().toLowerCase(Locale.getDefault()).trim(' ')
        act.noteListSelected = act.noteList
            .filter {(it.content ?: "").toLowerCase(Locale.getDefault()).contains(keywords)}
            .filter {(it.tags ?: "").matches(Regex("^[ ,]*$"))}
            .toMutableList()
        if (act.noteListSelected.isNotEmpty()) {
            if (keywords.isEmpty()) {
                    act.searchDescription = resources.getString(R.string.untagged_posts)
                } else {
                    act.searchDescription = String.format(resources.getString(R.string.untagged_posts_involving), keywords)
                }
            findNavController().navigate(R.id.action_search_to_list)
        } else {
            Toast.makeText(context, "No notes match your search.", Toast.LENGTH_LONG).show()
        }
    }

    fun tagsMatch(allTags: List<String>, selectedTags: List<String>): Boolean {
        return selectedTags.all {allTags.contains(it)}
    }
}