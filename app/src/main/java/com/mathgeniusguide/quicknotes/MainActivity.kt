package com.mathgeniusguide.quicknotes

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.mathgeniusguide.quicknotes.database.Note
import com.mathgeniusguide.quicknotes.database.Tag
import com.mathgeniusguide.quicknotes.util.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = "Quick Notes"
    lateinit var navController: NavController
    lateinit var database: DatabaseReference
    var noteList = emptyList<Note>().toMutableList()
    var noteListSelected = emptyList<Note>().toMutableList()
    var noteSelected = Note.create()
    var tagList = emptyList<Tag>().toMutableList()
    var searchDescription = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = findNavController(nav_host_fragment)
        toolbar.setupWithNavController(navController)
        tabs.setupWithNavController(navController)

        tabs.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.note) {
                noteSelected = Note.create()
                noteSelected.id = ""
                noteSelected.tags = ""
                noteSelected.time = ""
                noteSelected.content = ""
            }
            it.onNavDestinationSelected(navController)
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        drawer_view.setNavigationItemSelectedListener {
            it.onNavDestinationSelected(navController)
        }

        // Firebase
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance().reference
        database.orderByKey().addListenerForSingleValueEvent(itemListener)
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val notes = dataSnapshot.child(Constants.NOTES).children.iterator()
        while (notes.hasNext()) {
            val currentItem = notes.next()
            val note = Note.create()
            val map = currentItem.getValue() as HashMap<String, Any>
            note.id = currentItem.key
            note.time = map.get("time") as String?
            note.content = map.get("content") as String?
            note.tags = map.get("tags") as String?
            noteList.add(note)
            for (tag in (note.tags ?: "").split(",")) {
                tagList.add(Tag(tag, false))
            }
        }
        tagList = tagList.distinctBy {it.id}.filter { it.id.length <= 20 && it.id.isNotEmpty()}.toMutableList()
        tagList.sortBy {it.id}
        noteList.sortByDescending { it.time }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
