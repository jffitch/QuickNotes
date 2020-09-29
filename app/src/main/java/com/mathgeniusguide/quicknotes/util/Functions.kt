package com.mathgeniusguide.quicknotes.util

import android.view.View
import android.widget.EditText

object Functions {
    // check whether list of fields have been filled
    fun filled(vararg fields: EditText): Boolean {
        return fields.all { it.text.isNotEmpty() }
    }

    // make a list of views visible
    fun show(vararg views: View) {
        for (i in views) {
            i.visibility = View.VISIBLE
        }
    }

    // make a list of views gone
    fun hide(vararg views: View) {
        for (i in views) {
            i.visibility = View.GONE
        }
    }
}