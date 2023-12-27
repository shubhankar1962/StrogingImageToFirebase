package com.example.scopedstorage

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scopedstorage.databinding.ActivityStatusBinding

class Status : AppCompatActivity() {

    lateinit var binding: ActivityStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)


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