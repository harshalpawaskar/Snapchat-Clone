package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText : EditText? = null
    var passwordEditText : EditText? = null
    val auth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextTextPassword)

        if(auth.currentUser!=null)
        {
            LogIn();
        }
    }

    fun goClicked(view : View)
    {
        //Login User
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        LogIn()
                    } else if(task.exception?.message.toString().contains("The password is invalid")) {
                        Toast.makeText(applicationContext,task.exception?.message?.toString(),Toast.LENGTH_SHORT).show()
                    }else{
                        //Sign up user
                        auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        //Toast.makeText(applicationContext,"Success!!!",Toast.LENGTH_SHORT).show()
                                        FirebaseDatabase.getInstance().getReference().child("users").child(task.result!!.user.uid).child("email").setValue(emailEditText?.text.toString())
                                        LogIn()
                                    } else {
                                        Toast.makeText(applicationContext,"Error!!!",Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }

                }



    }

    fun LogIn()
    {
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
        finish()
    }
}