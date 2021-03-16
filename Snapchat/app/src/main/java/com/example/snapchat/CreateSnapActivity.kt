package com.example.snapchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.*
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    var uploadImageView : ImageView? = null
    var messageEditText : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    fun chooseImageClicked(view: View)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else getPhoto()
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedUri = data!!.data
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedUri)
                uploadImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        uploadImageView = findViewById(R.id.uploadImageView)
        messageEditText = findViewById(R.id.uploadSnapMessageEditText)
    }

    fun nextButtonClicked(view: View)
    {
        uploadImageView?.isDrawingCacheEnabled = true
        uploadImageView?.buildDrawingCache()
        val bitmap = (uploadImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()



        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Issue", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot->
            taskSnapshot.metadata!!.reference?.downloadUrl?.addOnCompleteListener{task ->

                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                var downloadUrl =task.result!!.toString()
                Log.i("Image Url:",downloadUrl);

                val intent = Intent(this,ChooseUserActivity :: class.java)
                intent.putExtra("imageUrl",downloadUrl);
                intent.putExtra("imageName",imageName);
                intent.putExtra("message",messageEditText?.text.toString());
                startActivity(intent)
            }
        })
    }
}