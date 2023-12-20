package com.kaansimsek.kotlinproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController



class AdActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val REQUEST_IMAGE_PICK = 1
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    data class Animal(
        val name: String = "",
        val bio: String = "",
        val gender: String = "",
        val age: Int? = null,
        val race: String = "",
        val photoUrl: String? = null,
        val userUid: String? = null
    )

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        val ppButton = findViewById<FloatingActionButton>(R.id.ppButton)
        val animalPhoto = findViewById<ShapeableImageView>(R.id.animalphoto)

        ppButton.setOnClickListener {
            // Kullanıcıya galeriyi açma izni veren intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        val publishButton = findViewById<Button>(R.id.publishButton)
        publishButton.setOnClickListener {
            uploadPhotoAndDataToFirebase()
        }

        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val genderOptions = resources.getStringArray(R.array.gender_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerGender.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            // Seçilen fotoğrafın URI'sini al
            selectedImageUri = data.data

            // URI'yi animalphoto ImageView'e yansıt
            val animalPhoto = findViewById<ShapeableImageView>(R.id.animalphoto)
            animalPhoto.setImageURI(selectedImageUri)
        }
    }

    private fun uploadPhotoAndDataToFirebase() {
        if (selectedImageUri != null) {
            // Fotoğrafın yükleneceği storage yolunu belirle
            val userUid = FirebaseAuth.getInstance().currentUser?.uid
            val photoRef = storageReference.child("animal_photos/${userUid}_${UUID.randomUUID()}.jpg")

            // Fotoğrafı yükle
            photoRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Yükleme başarılı olduğunda, URL'yi Firestore veritabanına kaydet
                    taskSnapshot.storage.downloadUrl
                        .addOnSuccessListener { downloadUrl ->
                            saveDataToFirebase(downloadUrl.toString(), userUid)
                        }
                }
                .addOnFailureListener { e ->
                    // Yükleme başarısız olduğunda hata mesajını göster
                    Toast.makeText(this, "Fotoğraf yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Kullanıcı bir fotoğraf seçmediyse hata mesajı göster
            Toast.makeText(this, "Lütfen bir fotoğraf seçin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToFirebase(photoUrl: String, userUid: String?) {
        val nameEditText = findViewById<EditText>(R.id.nametext)
        val bioEditText = findViewById<EditText>(R.id.biotext)
        val agetext = findViewById<EditText>(R.id.agetext)
        val racetext = findViewById<EditText>(R.id.racetext)
        val genderSpinner = findViewById<Spinner>(R.id.spinnerGender)

        val name = nameEditText.text.toString()
        val bio = bioEditText.text.toString()
        val age = agetext.text.toString().toIntOrNull()
        val race = racetext.text.toString()
        val gender = genderSpinner.selectedItem.toString()

        val animal = Animal(name, bio, gender, age, race, photoUrl, userUid)

        firestore.collection("animals")
            .add(animal)
            .addOnSuccessListener { documentReference ->
                // Handle success, if needed
                // documentReference.id contains the ID of the newly added document
                Toast.makeText(this, "İlan başarıyla eklendi", Toast.LENGTH_SHORT).show()
                findNavController(R.id.homefragment).navigate(R.id.homefragment)


            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(this, "İlan eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
            }
    }


}
