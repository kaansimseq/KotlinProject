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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class AdActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val REQUEST_IMAGE_PICK = 1
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private var db = Firebase.firestore


    data class Animal(
        val name: String = "",
        val bio: String = "",
        val gender: String = "",
        val age: Int? = null,
        val race: String = "",
        val photoUrl: String? = null,
        val userUid: String? = null,
        val username: String? = null,
        val userphonenumber: String? = null,
        val location: String = ""
    )

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        val ppButton = findViewById<FloatingActionButton>(R.id.ppButton)
        val animalPhoto = findViewById<ShapeableImageView>(R.id.animalphoto)

        ppButton.setOnClickListener {
            ImagePicker.with(this)
                .crop(1f,1f)	    	            //Crop image(Optional), Check Customization for more option
                .compress(1024)			        //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
            /*
            // Kullanıcıya galeriyi açma izni veren intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
             */
        }

        val publishButton = findViewById<Button>(R.id.publishButton)
        publishButton.setOnClickListener {

            val nameEditText = findViewById<EditText>(R.id.nametext)
            val bioEditText = findViewById<EditText>(R.id.biotext)
            val agetext = findViewById<EditText>(R.id.agetext)
            val racetext = findViewById<EditText>(R.id.racetext)
            val locationtext = findViewById<EditText>(R.id.locationtext)
            val genderSpinner = findViewById<Spinner>(R.id.spinnerGender)

            val name = nameEditText.text.toString()
            val bio = bioEditText.text.toString()
            val age = agetext.text.toString()
            val race = racetext.text.toString()
            val location = locationtext.text.toString()
            val gender = genderSpinner.selectedItem.toString()

            if(bio.isNotEmpty() && name.isNotEmpty() && race.isNotEmpty() && location.isNotEmpty() && gender.isNotEmpty() && age.isNotEmpty()){
                uploadPhotoAndDataToFirebase()
            }else{
                Toast.makeText(this, "Fields cannot be empty!!", Toast.LENGTH_SHORT).show()
            }

        }

        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val genderOptions = resources.getStringArray(R.array.gender_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerGender.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Seçilen fotoğrafın URI'sini al
            selectedImageUri = data?.data

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


        val userUid = FirebaseAuth.getInstance().currentUser?.uid

        if (userUid != null) {
            val ref = db.collection("users").document(userUid)
            ref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val username = documentSnapshot.getString("fullName")
                    val userphonenumber = documentSnapshot.getString("phoneNumber")

                    val nameEditText = findViewById<EditText>(R.id.nametext)
                    val bioEditText = findViewById<EditText>(R.id.biotext)
                    val agetext = findViewById<EditText>(R.id.agetext)
                    val racetext = findViewById<EditText>(R.id.racetext)
                    val locationtext = findViewById<EditText>(R.id.locationtext)
                    val genderSpinner = findViewById<Spinner>(R.id.spinnerGender)

                    val name = nameEditText.text.toString()
                    val bio = bioEditText.text.toString()
                    val age = agetext.text.toString().toIntOrNull()
                    val race = racetext.text.toString()
                    val location = locationtext.text.toString()
                    val gender = genderSpinner.selectedItem.toString()


                    val animal = Animal(name, bio, gender, age, race, photoUrl, userUid,username,userphonenumber,location)

                    firestore.collection("animals")
                        .add(animal)
                        .addOnSuccessListener { documentReference ->

                            Toast.makeText(this, "İlan başarıyla eklendi", Toast.LENGTH_SHORT).show()
                            findNavController(R.id.homefragment).navigate(R.id.homefragment)


                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "İlan eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                        }

                }
            }.addOnFailureListener { e ->
            }
        }



    }


}
