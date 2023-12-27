package com.example.scopedstorage

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.scopedstorage.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseFireStore: FirebaseFirestore
    lateinit var storageinstance : StorageReference
    private var imageUri:Uri? = null


    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        //imageUri = uri
        if (uri != null) {
            val intent = Intent(this, Status::class.java)
            intent.putExtra("statusMedia", uri.toString())
            startActivity(intent)
            Log.d("TAG", "photo selected")
        } else {
            Log.d("TAG", "photo can't selected")
        }

    }
    
    private val openGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result ->
               if(result != null){
                val data = result
                data.let { uri ->
                    // Proceed with the upload
                    uploadToFirebase(uri)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createFile.setOnClickListener{
            createFile();
        }

        binding.getImg.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }
            getImage()
        }

        init()

        binding.uploadImg.setOnClickListener{
            uploadImgFirebase()
        }
    }


    private fun init()
    {
        storageinstance = Firebase.storage.reference
        firebaseFireStore = FirebaseFirestore.getInstance()
    }





    fun createFile()
    {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, binding.writeTitle.text.toString())
        createFile.launch(intent)
    }

    fun createFile(uri:Uri)
    {
        try{
            val parcelFileDescriptor = this.contentResolver.openFileDescriptor(uri,"w")
            val fileoutputStream = FileOutputStream(parcelFileDescriptor?.fileDescriptor,)
            fileoutputStream.write(binding.writeTxt.text.toString().toByteArray())
            fileoutputStream.close()
            parcelFileDescriptor?.close()
        }
        catch (e:Exception)
        {
             e.printStackTrace()
        }
    }

    fun getImage(){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

    }

    private fun displayImg(uri: Uri) {
        binding.showImage.setImageURI(uri)
    }

    private val createFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        it.data?.data.let {
            createFile(it!!)
        }
    }

//    private fun uploadImgFirebase() {
//        //getImage()
//        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)
//        {
//            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
//        }
//
//        openGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
//        storageinstance.child(System.currentTimeMillis().toString())
//        imageUri?.let {
//            storageinstance.putFile(it).addOnCompleteListener{task->
//                if(task.isSuccessful)
//                {
//                    storageinstance.downloadUrl.addOnSuccessListener {
//                        val map = HashMap<String, Any>()
//                        map["pics"] = it.toString()
//
//                        firebaseFireStore.collection("StatusImages").add(map).addOnCompleteListener {firestoreTask->
//                            if(firestoreTask.isSuccessful)
//                            {
//                                Toast.makeText(this,"successfully uploaded", Toast.LENGTH_SHORT).show()
//                            }else{
//                                Toast.makeText(this,"uploade failed", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }else{
//                    Toast.makeText(this,"error uploaded", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//        }
//    }


    private fun uploadImgFirebase() {
        // Check and request READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            // Launch gallery to pick media
            openGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }



    // Function to handle the upload process
    private fun uploadToFirebase(uri: Uri) {
        // Create a reference to the storage location with a unique filename
        val storageReference = storageinstance.child(System.currentTimeMillis().toString())

        storageReference.putFile(uri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // File uploaded successfully, get the download URL
                Toast.makeText(this,"successfully uploaded", Toast.LENGTH_SHORT).show()
                storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Save the download URL to Firestore
                    saveToFirestore(downloadUrl.toString())
                }
            } else {
                // Handle the error
                Toast.makeText(this, "Error uploading file: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save the download URL to Firestore
    private fun saveToFirestore(downloadUrl: String) {
        val map = HashMap<String, Any>()
        map["pics"] = downloadUrl

        firebaseFireStore.collection("StatusImages").add(map).addOnCompleteListener { firestoreTask ->
            if (firestoreTask.isSuccessful) {
                Toast.makeText(this, "Successfully uploaded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


}