package com.example.scopedstorage

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.scopedstorage.databinding.ActivityStatusBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Status : AppCompatActivity() {

    lateinit var binding: ActivityStatusBinding
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("statusimage")

        val statusMediafile = intent.getStringExtra("statusMedia")
        if(statusMediafile != null)
        {
            val mediaUri = Uri.parse(statusMediafile)
            binding.statuscreen.setImageURI(mediaUri)

        }

        retriveImageDataFromFireBase()
    }

    private fun retriveImageDataFromFireBase() {

    }
}