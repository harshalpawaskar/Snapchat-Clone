package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChooseUserActivity : AppCompatActivity() {

    var chooseUsersListView : ListView? = null
    var emails : ArrayList<String> = ArrayList()
    var keys : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUsersListView = findViewById(R.id.chooseUsersListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        chooseUsersListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emails.add(snapshot.child("email").value.toString())
                keys.add(snapshot.key.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })

        chooseUsersListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapMap : Map<String,String> = mapOf("from" to FirebaseAuth.getInstance().currentUser.email,"imageName" to intent.getStringExtra("imageName"),
                    "imageUrl" to intent.getStringExtra("imageUrl"),"message" to intent.getStringExtra("message"))

            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(i)).child("snaps").push().setValue(snapMap)

            val intent = Intent(this,SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}