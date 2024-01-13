package com.kaansimsek.kotlinproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kaansimsek.kotlinproject.databinding.ActivityPhotoAndBioBinding

class PhotoAndBioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoAndBioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private var db = Firebase.firestore

    private var biography: String = ""
    private var imageUri: Uri? = null

    //Saving data in database
    private fun saveDatabase(fullName: String?, phoneNumber: String?, birthday: String?, email: String?, password: String?, biography: String, imageUri: Uri?){

        val userMap = hashMapOf(
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
            "birthday" to birthday,
            "email" to email,
            "password" to password,
            "biography" to biography
        )

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(userID).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this,"Successfully saved on database!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed!!", Toast.LENGTH_SHORT).show()
            }

        // Profil resmini Firebase Storage'a yükleme
        if (imageUri != null) {
            saveImageToStorage(userID, imageUri)
        }

    }

    private fun saveImageToStorage(userID: String, imageUri: Uri) {
        // Firebase Storage referansı oluşturma
        val storageReference = FirebaseStorage.getInstance().reference.child("profile_images/$userID.jpg")

        // Resmi yükleme
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Resim URL'sini alır ve Firestore'a kaydeder
                    saveImageUrlToDatabase(userID, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToDatabase(userID: String, imageUrl: String) {
        // Resim URL'sini Firebase Firestore'a kaydetme işlemi
        db.collection("users").document(userID)
            .update("imageUrl", imageUrl)
    }

    //Profile Photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Seçilen resmin URI'sini alır
            imageUri = data?.data

            // ImageView'e resmi gösterir
            binding.ppImage.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoAndBioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        val intent = intent
        val fullName = intent.getStringExtra("fullName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val birthday = intent.getStringExtra("birthday")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        //ImagePicker kütüphanesi sayesinde resim seçmeyi ve uygulamada kullanmamızı sağlar
        binding.ppButton.setOnClickListener{
            ImagePicker.with(this)
                .crop(1f,1f)	    	            //Crop image(Optional), Check Customization for more option
                .compress(1024)			        //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        binding.signupButton.setOnClickListener {

            biography = binding.biographyEditText.text.toString()

            if(biography.isNotEmpty() && imageUri != null){

                //Verileri alır database kaydeder
                saveDatabase(fullName,phoneNumber,birthday,email,password,biography,imageUri)

                //Email Verification
                firebaseAuth.currentUser?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        Toast.makeText(this,"Please Verify your Email!",Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener{
                        Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this, VerifyEmailActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this, "Fields cannot be empty!!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}