package com.example.jamapp.MyEvents


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jamapp.Homescreen.HomeEventAdapter
import com.example.jamapp.Model.Event
import com.example.jamapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass.
 */
class my_events : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db : DatabaseReference

    private lateinit var linearLayoutManager : LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize db
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference
        val eventRef = db.child("event")

        val rootView =  inflater.inflate(R.layout.fragment_home, container, false) as View

        val recyclerView = rootView.findViewById(R.id.Recycler) as RecyclerView

        linearLayoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = linearLayoutManager

        val events = arrayListOf<Event>()
        val adapter = HomeEventAdapter(events, context!!)
        recyclerView.adapter = adapter

        // Get events
        eventRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }

            // When events are added
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the list first
                events.clear()

                // Get event data
                for (item in dataSnapshot.children) {
                    val event = item.getValue(Event::class.java) as Event
                    // Check if user has registered in that event
                    if (isRegistered(event)) {
                        // If yes, add that event to be displayed
                        events.add(event)
                    }
                }
                // Update adapter
                adapter.notifyDataSetChanged()
            }
        })



        // Inflate the layout for this fragment
        return view
    }

    fun isRegistered(event : Event) : Boolean {
        var isRegistered = false
        val ref = db.child("event").child(event.event_id).child("Attendees").child(auth.currentUser!!.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError : DatabaseError) {
                Log.w("Error", databaseError.toString())
            }

            override fun onDataChange(snapshot : DataSnapshot) {
                Log.d("Attendees","Checking for existence")

                // Check if User hasn't joined Event
                if (!snapshot.exists()) {
                    Log.d("Attendees","User not registered")
                    isRegistered = false
                    // Check if User has joined Event
                } else {
                    Log.d("Attendees","User already registered")
                    isRegistered = true
                }
            }
        })

        return isRegistered
    }

    fun createEvent(view : View) {
        // tbc
    }
}
