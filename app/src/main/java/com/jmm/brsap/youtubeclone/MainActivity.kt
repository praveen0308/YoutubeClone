package com.jmm.brsap.youtubeclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.jmm.brsap.youtubeclone.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val VIDEO : Int = 3
    lateinit var uri : Uri
    private lateinit var videoUrl:String
    private var thumbnailUrl:String = "N.A."

    val storage = Firebase.storage
    lateinit var mStorage : StorageReference
    private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnUpload.setOnClickListener {
                val title = etTitle.text.toString()
                val description = etDescription.text.toString()

                val myVideo = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "videoUrl" to videoUrl,
                    "thumbnailUrl" to thumbnailUrl,
                    "categoryId" to 1,
                    "isActive" to 1,

                )

                db.collection("videos")
                    .add(myVideo)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("MainActivity", "Error adding document", e)
                    }

            }
        }

        binding.uploadVideo.setOnClickListener {

            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO)
        }

// Add a new document with a generated ID


//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    textView.text = "${document.id} => ${document.data}"
//                    Log.d("MainActivity", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("MainActivity", "Error getting documents.", exception)
//            }
//        db.collection("users")
//            .document("0lABWakA2sH9Em94Zioz").get().addOnSuccessListener {
//                binding.data.text = it.data.toString()
//            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
        if (requestCode == VIDEO) {
                uri = data!!.data!!

                Log.w("URI",uri.toString())
            upload()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        mStorage = storage.reference
        var mReference = uri.lastPathSegment?.let { mStorage.child("videos/$it") }
        try {
            mReference?.putFile(uri)?.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                mReference.downloadUrl.addOnSuccessListener {
                    videoUrl = it.toString()
                }
//                var url = taskSnapshot!!.metadata?.path
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }
}