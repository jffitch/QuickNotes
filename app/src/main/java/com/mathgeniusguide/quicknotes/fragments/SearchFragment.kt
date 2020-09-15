package com.mathgeniusguide.quicknotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import com.mathgeniusguide.quicknotes.adapter.TagAdapter
import kotlinx.android.synthetic.main.search_fragment.*

class SearchFragment: Fragment() {
    lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tagsHalves = act.tagList.chunked((act.tagList.size / 2 + .5).toInt())
        tagsRV0.layoutManager = LinearLayoutManager(context)
        tagsRV0.adapter = TagAdapter(tagsHalves[0], act)
        tagsRV1.layoutManager = LinearLayoutManager(context)
        tagsRV1.adapter = TagAdapter(tagsHalves[1], act)
    }
}